package top.jocularchao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.jocularchao.entity.dto.Account;
import top.jocularchao.mapper.AccountMapper;
import top.jocularchao.service.AccountService;

/**
 * @author jocularchao
 * @date 2024-01-03 21:19
 * @description
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if (account == null){
            throw new UsernameNotFoundException("用户名或邮箱错误");
        }


        return User
                .withUsername(username) //登录的是邮箱那么返回的也是邮箱
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }


    public Account findAccountByNameOrEmail(String text){

        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        return null;
    }
}
