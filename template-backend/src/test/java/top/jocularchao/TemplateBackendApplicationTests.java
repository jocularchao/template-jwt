package top.jocularchao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import top.jocularchao.service.RedisService;

@SpringBootTest
class TemplateBackendApplicationTests {

	@Autowired
	private RedisService redisService;

	@Test
	void contextLoads() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		//$2a$10$RQ4LuWr5hL73VDxztxJGu.lAwCTHustBif5sKiKK9866u.1m1LUaS
		System.out.println(encoder.encode("123456"));
	}

	//测试redis
	@Test
	void testRedisOperations(){
		String key = "test";
		String value = "test";

		// Set a key
		redisService.setKey(key, value);

		// Get the key
		String retrievedValue = redisService.getKey(key);

		System.out.println(value.equals(retrievedValue));
	}

}
