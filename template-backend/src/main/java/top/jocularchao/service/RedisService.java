package top.jocularchao.service;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author jocularchao
 * @date 2024-01-03 19:57
 * @description
 */
@Service
public class RedisService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void setKey(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}