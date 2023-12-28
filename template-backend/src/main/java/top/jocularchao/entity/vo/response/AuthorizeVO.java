package top.jocularchao.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author jocularchao
 * @date 2023-12-28 22:30
 * @description 封装返回给客户端的用户的详细信息
 */
@Data
public class AuthorizeVO {

    String username;    //用户名
    String role;        //角色
    String token;       //token
    Date expire;        //过期时间


}
