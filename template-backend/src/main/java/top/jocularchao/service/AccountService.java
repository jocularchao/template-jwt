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
}
