package se.yg.microservices.composite.product;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import se.yg.api.core.product.Product;
import se.yg.api.core.recommendation.Recommendation;
import se.yg.api.core.review.Review;
import se.yg.microservices.composite.product.service.ProductCompositeIntegration;
import se.yg.util.exceptions.InvalidInputException;
import se.yg.util.exceptions.NotFoundException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ProductCompositeServiceApplicationTests {
	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;


	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductCompositeIntegration compositeIntegration;

	@BeforeEach
	public void setUp() {

		when(compositeIntegration.getProduct(PRODUCT_ID_OK)).
				thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));
		when(compositeIntegration.getRecommendations(PRODUCT_ID_OK)).
				thenReturn(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock-address")));
		when(compositeIntegration.getReviews(PRODUCT_ID_OK)).
				thenReturn(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock-address")));
		when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));
		when(compositeIntegration.getProduct(PRODUCT_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
	}

	@Test
	void getProductByID()  {
		// WebTestClient 사용법
		//https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/
		client.get()
				.uri("/product-composite/" + PRODUCT_ID_OK)
				.accept(MediaType.APPLICATION_JSON)
				.exchange() // 위의 요청을 수행 그리고 응답을 검증하기 위해 이후 체이닝으로 검사

//				// PRINT ............
//				.expectHeader().value("content-Type", result->{System.out.println(result);})
//				.expectBodyList(Map.class)
//				.consumeWith(response->{
//					List<Map> tmp = response.getResponseBody();
//					System.out.println("response body --->\n " + tmp);
//				});

				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
				.jsonPath("$.recommendations.length()").isEqualTo(1)
				.jsonPath("$.reviews.length()").isEqualTo(1)
				.consumeWith(result -> {
					System.out.println("============Req/Rsp============");
					System.out.println(result.toString());
					System.out.println("================================");
				});


	}

	@Test
	public void getProductNotFound(){
		client.get()
			.uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
			.accept(MediaType.APPLICATION_JSON)
			.exchange() // 위의 요청을 수행 그리고 응답을 검증하기 위해 이후 체이닝으로 검사
			.expectStatus().isNotFound()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
			.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND)
			.consumeWith(result -> {
				System.out.println("============Req/Rsp============");
				System.out.println(result.toString());
				System.out.println("================================");
			});
	}

	@Test
	public void getProductInvalidInput() {

		client.get()
				.uri("/product-composite/" + PRODUCT_ID_INVALID)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}

}
