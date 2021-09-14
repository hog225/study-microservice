package se.yg.microservices.composite.product;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.yg.api.composite.product.ProductAggregate;
import se.yg.api.composite.product.RecommendationSummary;
import se.yg.api.composite.product.ReviewSummary;
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
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"eureka.client.enabled=false"})
@AutoConfigureWebTestClient
class ProductCompositeServiceApplicationTests {
	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;


	@Autowired
	private WebTestClient client;



	@MockBean
	private ProductCompositeIntegration compositeIntegration;

//	@BeforeEach
//	public void setUp() {
//
//
//		when(compositeIntegration.getProduct(PRODUCT_ID_OK)).
//				thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));
//		when(compositeIntegration.getRecommendations(PRODUCT_ID_OK)).
//				thenReturn(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock-address")));
//		when(compositeIntegration.getReviews(PRODUCT_ID_OK)).
//				thenReturn(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock-address")));
//		when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));
//		when(compositeIntegration.getProduct(PRODUCT_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
//
//		// TODO 왜지 ? Create 의 경우 아래 MockBean의 아래 리턴을 선언해 주지 않아도 정상 동작함 ..
////		when(compositeIntegration.createProduct(new Product())).
////				thenReturn(new Product());
////		when(compositeIntegration.createRecommendation(new Recommendation())).
////				thenReturn(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock-address"));
////		when(compositeIntegration.createReview(new Review())).
////				thenReturn(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock-address"));
//
//	}
	@BeforeEach
	public void setUp() {

		when(compositeIntegration.getProduct(PRODUCT_ID_OK)).
				thenReturn(Mono.just(new Product(PRODUCT_ID_OK, "name", 1, "mock-address")));

		when(compositeIntegration.getRecommendations(PRODUCT_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address"))));

		when(compositeIntegration.getReviews(PRODUCT_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address"))));

		when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

		when(compositeIntegration.getProduct(PRODUCT_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
	}
	@Test
	public void createCompositeProduct1() {

		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1, null, null, null);

		postAndVerifyProduct(compositeProduct, OK);
	}

	@Test
	public void createCompositeProduct2() {
		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
				singletonList(new RecommendationSummary(1, "a", 1, "c")),
				singletonList(new ReviewSummary(1, "a", "s", "c")), null);

		postAndVerifyProduct(compositeProduct, OK);
	}

	@Test
	public void deleteCompositeProduct() {
		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
				singletonList(new RecommendationSummary(1, "a", 1, "c")),
				singletonList(new ReviewSummary(1, "a", "s", "c")), null);

		postAndVerifyProduct(compositeProduct, OK);

		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
	}



	@Test
	void getProductByID()  {
		// WebTestClient 사용법
		//https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/
		client.get()
				.uri("/product-composite/" + PRODUCT_ID_OK)
				.accept(APPLICATION_JSON)
				.exchange() // 위의 요청을 수행 그리고 응답을 검증하기 위해 이후 체이닝으로 검사

//				// PRINT ............
//				.expectHeader().value("content-Type", result->{System.out.println(result);})
//				.expectBodyList(Map.class)
//				.consumeWith(response->{
//					List<Map> tmp = response.getResponseBody();
//					System.out.println("response body --->\n " + tmp);
//				});

				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
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

//	@Test
//	public void getProductNotFound(){
//		client.get()
//			.uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
//			.accept(APPLICATION_JSON)
//			.exchange() // 위의 요청을 수행 그리고 응답을 검증하기 위해 이후 체이닝으로 검사
//			.expectStatus().isNotFound()
//			.expectHeader().contentType(APPLICATION_JSON)
//			.expectBody()
//			.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
//			.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND)
//			.consumeWith(result -> {
//				System.out.println("============Req/Rsp============");
//				System.out.println(result.toString());
//				System.out.println("================================");
//			});
//	}
//
//	@Test
//	public void getProductInvalidInput() {
//
//		client.get()
//				.uri("/product-composite/" + PRODUCT_ID_INVALID)
//				.accept(APPLICATION_JSON)
//				.exchange()
//				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
//				.expectHeader().contentType(APPLICATION_JSON)
//				.expectBody()
//				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
//				.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
//	}

	// TODO  왜 안되는 지 모르겠음
	@Test
	public void getProductNotFound() {

		getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
	}

	@Test
	public void getProductInvalidInput() {

		getAndVerifyProduct(PRODUCT_ID_INVALID, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}
	private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/product-composite/" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
		client.post()
				.uri("/product-composite")
				.body(just(compositeProduct), ProductAggregate.class)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		client.delete()
				.uri("/product-composite/" + productId)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus);
	}
}
