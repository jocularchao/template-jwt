package top.jocularchao.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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


    @Resource(name = "stringRedisTemplate")
    StringRedisTemplate redisTemplate;



    //创建令牌
    public String createJwt(UserDetails details,int userId,String username){  //从userDetails来获取用户的信息
        //生成用户加密的算法
        Algorithm algorithm = Algorithm.HMAC256(key);   //加载密钥

        Date expire = this.expireTime();    //获取过期时间值

        return JWT
                .create()   //创建
                .withJWTId(UUID.randomUUID().toString())    //生成随机id便于在销毁jwt时处理
                .withClaim("id", userId)   //userDetails里面没有用户的id，只有用户的名字，所以需要单独加进来加上形参 id
                .withClaim("name", username) //我们是通过用户名/邮箱登录的，所以userDetails有可能是邮箱，不太准确同上
                //details.getAuthorities()是一个list形式，我们要通过GrantedAuthority的方法把它变成string类型的权限
                .withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)    //过期时间
                .withIssuedAt(new Date())   //现在的token的颁发时间
                .sign(algorithm);   //用算法签名得到lwt令牌
    }

    //销毁令牌
    public boolean invalidateJwt(String headerToken){
        String token = this.convertToken(headerToken);
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier  jwtVerifier = JWT.require(algorithm).build();

        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            return deleteToken(verify.getId(),verify.getExpiresAt());

        }catch (JWTVerificationException e){
            return false;
        }

    }

    //删除令牌
    private boolean deleteToken(String uuid,Date time){
        if (this.isInvalidToken(uuid)){
            return false;
        }
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(),0);   //如果已经过期了，那么值就为负值，不能给到token，就用0替代

        redisTemplate.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;

    }

    //判断令牌是否过期
    private boolean isInvalidToken(String uuid){
        //查询uuid是否在黑名单列表中
        return Boolean.TRUE.equals(redisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }


    //过期时间
    public Date expireTime(){
        //Calendar提供了一个日历的功能
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);   //加载过期时间值

        return calendar.getTime();
    }

    //解析jwt
    public DecodedJWT resolveJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if (token == null)
            return null;
        //token解析的算法 与生成时的算法一致
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier  jwtVerifier = JWT.require(algorithm).build();

        try {
            //验证当前的jwt是否合法，就是看是否被用户篡改过，若是篡改过就会抛一个验证异常的运行时异常
            DecodedJWT verifier = jwtVerifier.verify(token);
            //查看jwt令牌是否失效
            if (this.isInvalidToken(verifier.getId())){
                return null;
            }
            //查看jwt令牌是否过期了
            Date expires = verifier.getExpiresAt();

            return new Date().after(expires) ? null:verifier;
        }catch (JWTVerificationException e){
            return null;
        }


    }

    //Authorization中带的是Bear token 所以我们要判断一下是否为这个格式，并把token剪切出来
    private String convertToken(String headerToken){
        if (headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);

    }

    //解析token中包含的用户信息
    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();

        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities("authorities")
                .build();
    }

    //解析id值
    public Integer toId(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();

        return claims.get("id").asInt();
    }

}
