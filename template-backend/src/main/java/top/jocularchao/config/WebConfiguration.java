package top.jocularchao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author jocularchao
 * @date 2024-01-03 21:42
 * @description
 */
@Configuration
public class WebConfiguration {

    //密码加密器
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
