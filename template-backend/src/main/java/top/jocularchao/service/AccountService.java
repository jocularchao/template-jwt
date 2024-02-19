package top.jocularchao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import top.jocularchao.entity.dto.Account;

/**
 * @author jocularchao
 * @date 2024-01-03 21:18
 * @description
 */
public interface AccountService extends IService<Account> , UserDetailsService {

    //根据用户名或邮箱查找用户
    Account findAccountByNameOrEmail(String text);

    //注册邮件验证码

    /**
     *
     * @param type 邮件类型
     * @param email 邮件地址
     * @param ip    用户的ip地址 通过它来限制发送邮件的频率
     * @return 返回的肯定就是要发送的邮箱验证码
     */
    String registerEmailVerifyCode(String type,String email,String ip);
}
