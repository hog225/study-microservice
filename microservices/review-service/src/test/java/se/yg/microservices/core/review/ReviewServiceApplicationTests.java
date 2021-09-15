package se.yg.microservices.core.review;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false"})
class ReviewServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
