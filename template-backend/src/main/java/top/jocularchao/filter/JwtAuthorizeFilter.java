package top.jocularchao.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.juli.OneLineFormatter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.jocularchao.utils.JwtUtils;

import java.io.IOException;

/**
 * @author jocularchao
 * @date 2023-12-29 17:49
 * @description
 */
@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {  //一次请求执行一次的普通过滤器

    //注入
    @Resource
    JwtUtils jwtUtils;

    //验证逻辑
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //从请求头里面读出来Authorization
        //header里面有一个Authorization的请求头，它携带"Bearer token"的字符串
        String authorization = request.getHeader("Authorization");
        //利用工具类解析请求头携带的token
        DecodedJWT jwt = jwtUtils.resolveJwt(authorization);
        if (jwt!=null){
            //利用工具类解析jwt为用户信息
            UserDetails user = jwtUtils.toUser(jwt);
            //这个token是spring security内部的一个token
            UsernamePasswordAuthenticationToken authenticationToken = new
                    UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //直接把验证信息往security context里面丢进去,验证就通过了
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //工具类解析id值
            request.setAttribute("id", jwtUtils.toId(jwt));
        }
        filterChain.doFilter(request,response);


    }
}
