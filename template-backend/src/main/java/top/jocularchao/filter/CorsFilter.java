package top.jocularchao.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.jocularchao.utils.Const;

import java.io.IOException;

/**
 * @author jocularchao
 * @date 2024-01-06 19:21
 * @description 跨域问题
 */
@Component
//spring security的过滤器链 它的优先级很高 默认-100，而跨域的过滤器 包括限流等功能 是要在它之前的
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request,response);
        //正常放行
        chain.doFilter(request,response);
    }

    //在响应头添加跨域信息
    private void addCorsHeader(HttpServletRequest request,
                               HttpServletResponse response){

        //允许哪些地址跨域访问        这个Origin是网络请求中前端的地址在浏览器的network中可以查到
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

        //添加允许的一些方法
        response.addHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
        //
        response.addHeader("Access-Control-Allow-Headers","Authorization, Content-Type");
    }
}
