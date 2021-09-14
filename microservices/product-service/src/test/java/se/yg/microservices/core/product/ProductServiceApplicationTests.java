package se.yg.microservices.core.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false"})
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
