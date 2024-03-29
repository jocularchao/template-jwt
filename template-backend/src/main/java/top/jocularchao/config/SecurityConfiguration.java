package top.jocularchao.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.jocularchao.entity.RestBean;
import top.jocularchao.entity.dto.Account;
import top.jocularchao.entity.vo.response.AuthorizeVO;
import top.jocularchao.filter.JwtAuthorizeFilter;
import top.jocularchao.service.AccountService;
import top.jocularchao.utils.JwtUtils;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author jocularchao
 * @date 2023-12-28 13:18
 * @description
 */
@Configuration
public class SecurityConfiguration {

    //注入jwt工具类
    @Resource
    JwtUtils jwtUtils;

    //注入jwt验证的过滤器
    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    //再次查询数据库
    @Resource
    AccountService service;

    //主配置
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //验证请求拦截和放行配置
                //爆红了背刺了，6.1版本更新这个方法过时了
                /*.authorizeHttpRequests()
                .anyRequest().permitAll()*/
                .authorizeHttpRequests(conf->conf
                        .requestMatchers("/api/auth/**").permitAll()    //所有验证的请求再用户没有登录的时候要允许通过
                        .anyRequest().authenticated()   //任何请求都不允许通过
                )
                //登录配置
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)  //登录成功的处理
                        .failureHandler(this::onAuthenticationFailure)  //登录失败的处理
                )
                //登出配置
                .logout(conf->conf
                        .logoutUrl("api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)    //登出成功的处理
                )
                //异常处理
                .exceptionHandling(conf->conf
                        .authenticationEntryPoint(this::onUnauthorized) //未登录配置
                        .accessDeniedHandler(this::onAccessDeny)  //登录但没权限
                )
                //取消CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                //因为我们现在是无状态的前后端分离跟有状态的前后端分离区别：session不需要去维护用户信息，因为用户信息都在JWT里面
                //所以我们把sessionManagement改成无状态，让security不去处理session
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //改成无状态
                )
                //添加自定义的过滤器
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();   //构建，返回默认的SecurityFilterChain对象
    }




    //登录成功后的处理
    /**
     *
     * @param request   请求
     * @param response  响应
     * @param authentication    请求成功后的验证信息
     * @throws IOException  io异常
     * @throws ServletException servlet异常
     */
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //配置编码
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        //拿到用户的详细信息
        User user = (User) authentication.getPrincipal();   //这里应该自己封装一个userdetails 不用springsecurity提供的user实现
        //根据拿到的用户 再次查询数据库进行验证并获得实体类用户信息
        Account account = service.findAccountByNameOrEmail(user.getUsername());
        //给用户塞一个令牌
        String token = jwtUtils.createJwt(user, account.getId(), account.getUsername());
        //封装用户信息，用于返回给客户端,把dto的数据 返回给vo，再由vo返回给客户端
        //AuthorizeVO vo = new AuthorizeVO();
        //1 拷贝信息
        //BeanUtils.copyProperties(account,vo);
        //2 自定义拷贝方法
        AuthorizeVO vo = account.asViewObject(AuthorizeVO.class,v->{
            v.setExpire(jwtUtils.expireTime());
            v.setToken(token);
        });


        //vo.setRole(account.getRole());

        //vo.setUsername(account.getUsername());

        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    //登录失败后的处理
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        //配置编码
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    //登出成功的处理
    //虽然登出成功和登陆成功的参数一样，但由于我们在登出的时候会写一个让jwt失效的功能，所以不能写在一起
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        //配置编码
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        //把writer 便于之后的判断
        PrintWriter writer = response.getWriter();

        String authorization = request.getHeader("Authorization");
        if (jwtUtils.invalidateJwt(authorization)){
            writer.write(RestBean.success("退出登录成功").asJsonString());
            return;
        }else {
            writer.write(RestBean.failure(400,"退出登录失败").asJsonString());
        }
    }

    //未登录的处理
    public void onUnauthorized(HttpServletRequest request,
                                HttpServletResponse response,
                                AuthenticationException exception) throws IOException {
        //配置编码
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    //登录但没权限
    public void onAccessDeny(HttpServletRequest request,
                                    HttpServletResponse response,
                                    AccessDeniedException accessDeniedException) throws IOException {
        //配置编码
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }

}
