package top.jocularchao.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * @author jocularchao
 * @date 2023-12-28 20:45
 * @description
 */
@Component
public class JwtUtils {

    //取一下配置文件配置好的密钥
    @Value("${spring.security.jwt.key}")
    String key;

    //从spring配置中取expire值作为过期时间 临时的7天之后要做续签
    @Value("${spring.security.jwt.expire}")
    int expire;



    public String createJwt(UserDetails details,int id,String username){  //从userDetails来获取用户的信息
        //生成用户加密的算法
        Algorithm algorithm = Algorithm.HMAC256(key);   //加载密钥

        Date expire = this.expireTime();    //获取过期时间值

        return JWT
                .create()   //创建
                .withClaim("id", id)   //userDetails里面没有用户的id，只有用户的名字，所以需要单独加进来加上形参 id
                .withClaim("name", username) //我们是通过用户名/邮箱登录的，所以userDetails有可能是邮箱，不太准确同上
                //details.getAuthorities()是一个list形式，我们要通过GrantedAuthority的方法把它变成string类型的权限
                .withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)    //过期时间
                .withIssuedAt(new Date())   //现在的token的颁发时间
                .sign(algorithm);   //用算法签名得到lwt令牌
    }

    //过期时间
    public Date expireTime(){
        //Calendar提供了一个日历的功能
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);   //加载过期时间值

        return calendar.getTime();
    }

}
