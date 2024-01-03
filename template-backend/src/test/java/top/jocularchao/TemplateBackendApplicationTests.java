package top.jocularchao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.jocularchao.service.RedisService;

@SpringBootTest
class TemplateBackendApplicationTests {

	@Autowired
	private RedisService redisService;

	@Test
	void contextLoads() {

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
