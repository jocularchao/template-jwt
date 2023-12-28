package top.jocularchao.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.jocularchao.entity.RestBean;
import top.jocularchao.entity.vo.response.AuthorizeVO;
import top.jocularchao.utils.JwtUtils;

import java.io.IOException;

/**
 * @author jocularchao
 * @date 2023-12-28 13:18
 * @description
 */
@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils jwtUtils;

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
                //取消CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                //因为我们现在是无状态的前后端分离跟有状态的前后端分离区别：session不需要去维护用户信息，因为用户信息都在JWT里面
                //所以我们把sessionManagement改成无状态，让security不去处理session
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //改成无状态
                )
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
        User user = (User) authentication.getPrincipal();
        //给用户塞一个令牌
        String token = jwtUtils.createJwt(user, 1, "林桑");
        //封装用户信息，用于返回给客户端
        AuthorizeVO vo = new AuthorizeVO();
        vo.setExpire(jwtUtils.expireTime());
        vo.setRole("");
        vo.setToken(token);
        vo.setUsername(user.getUsername());

        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    //登录失败后的处理
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        //配置编码
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.getWriter().write(RestBean.failure(401,exception.getMessage()).asJsonString());
    }

    //登出成功的处理
    //虽然登出成功和登陆成功的参数一样，但由于我们在登出的时候会写一个让jwt失效的功能，所以不能写在一起
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

    }

}
