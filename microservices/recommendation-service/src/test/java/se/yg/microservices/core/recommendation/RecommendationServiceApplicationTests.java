package se.yg.microservices.core.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"eureka.client.enabled=false", "eureka.client.enabled=false"})
class RecommendationServiceApplicationTests {


	@Autowired
	private WebTestClient client;

	@Test
	public void getRecommendationsByProductId() {

		int productId = 1;

		// 개별 Recormmendation 서비스에게 전송 테스트
		client.get()
				.uri("/recommendation?productId=" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(productId)
				.consumeWith(result -> {
					System.out.println("============Req/Rsp============");
					System.out.println(result.toString());
					System.out.println("================================");
				});
	}

}
