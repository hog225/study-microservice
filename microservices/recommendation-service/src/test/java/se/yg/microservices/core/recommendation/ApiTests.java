package se.yg.microservices.core.recommendation;

import org.assertj.core.internal.bytebuddy.build.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.yg.api.core.recommendation.Recommendation;
import se.yg.microservices.core.recommendation.repositories.RecommendationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;


@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false"})
public class ApiTests {
//
//    @Autowired
//    private WebTestClient client;
//
//    @Autowired
//    private RecommendationRepository repository;
//
//
//    @BeforeEach
//    public void setupDb() {
//        repository.deleteAll();
//    }
//
//    @Test
//    public void getRecommendationsByProductId() {
//
//        int productId = 1;
//
//        postAndVerifyRecommendation(productId, 1, OK);
//        postAndVerifyRecommendation(productId, 2, OK);
//        postAndVerifyRecommendation(productId, 3, OK);
//
//        assertEquals(3, repository.findByProductId(productId).size());
//
//        getAndVerifyRecommendationsByProductId(productId, OK)
//                .jsonPath("$.length()").isEqualTo(3)
//                .jsonPath("$[2].productId").isEqualTo(productId)
//                .jsonPath("$[2].recommendationId").isEqualTo(3);
//    }
//
//    @Test
//    public void duplicateError() {
//
//        int productId = 1;
//        int recommendationId = 1;
//
//        postAndVerifyRecommendation(productId, recommendationId, OK)
//                .jsonPath("$.productId").isEqualTo(productId)
//                .jsonPath("$.recommendationId").isEqualTo(recommendationId);
//
//        assertEquals(1, repository.count());
//
//        postAndVerifyRecommendation(productId, recommendationId, UNPROCESSABLE_ENTITY)
//                .jsonPath("$.path").isEqualTo("/recommendation")
//                .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Recommendation Id:1");
//
//        assertEquals(1, repository.count());
//    }
//
//    @Test
//    public void deleteRecommendations() {
//
//        int productId = 1;
//        int recommendationId = 1;
//
//        postAndVerifyRecommendation(productId, recommendationId, OK);
//        assertEquals(1, repository.findByProductId(productId).size());
//
//        deleteAndVerifyRecommendationsByProductId(productId, OK);
//        assertEquals(0, repository.findByProductId(productId).size());
//
//        deleteAndVerifyRecommendationsByProductId(productId, OK);
//    }
//
//    @Test
//    public void getRecommendationsMissingParameter() {
//
//        getAndVerifyRecommendationsByProductId("", BAD_REQUEST)
//                .jsonPath("$.path").isEqualTo("/recommendation")
//                .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
//    }
//
//    @Test
//    public void getRecommendationsInvalidParameter() {
//
//        getAndVerifyRecommendationsByProductId("?productId=no-integer", BAD_REQUEST)
//                .jsonPath("$.path").isEqualTo("/recommendation")
//                .jsonPath("$.message").isEqualTo("Type mismatch.");
//    }
//
//    @Test
//    public void getRecommendationsNotFound() {
//
//        getAndVerifyRecommendationsByProductId("?productId=113", OK)
//                .jsonPath("$.length()").isEqualTo(0);
//    }
//
//    @Test
//    public void getRecommendationsInvalidParameterNegativeValue() {
//
//        int productIdInvalid = -1;
//
//        getAndVerifyRecommendationsByProductId("?productId=" + productIdInvalid, UNPROCESSABLE_ENTITY)
//                .jsonPath("$.path").isEqualTo("/recommendation")
//                .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
//    }
//
//    private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(int productId, HttpStatus expectedStatus) {
//        return getAndVerifyRecommendationsByProductId("?productId=" + productId, expectedStatus);
//    }
//
//    private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(String productIdQuery, HttpStatus expectedStatus) {
//        return client.get()
//                .uri("/recommendation" + productIdQuery)
//                .accept(APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus)
//                .expectHeader().contentType(APPLICATION_JSON)
//                .expectBody()
//                .consumeWith(result -> {
//                    System.out.println("============Req/Rsp============");
//                    System.out.println(result.toString());
//                    System.out.println("================================");
//                });
//    }
//
//    private WebTestClient.BodyContentSpec postAndVerifyRecommendation(int productId, int recommendationId, HttpStatus expectedStatus) {
//        Recommendation recommendation = new Recommendation(productId, recommendationId, "Author " + recommendationId, recommendationId, "Content " + recommendationId, "SA");
//        return client.post()
//                .uri("/recommendation")
//                .body(just(recommendation), Recommendation.class)
//                .accept(APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus)
//                .expectHeader().contentType(APPLICATION_JSON)
//                .expectBody()
//                .consumeWith(result -> {
//                    System.out.println("============Req/Rsp============");
//                    System.out.println(result.toString());
//                    System.out.println("================================");
//                });
//    }
//
//    private WebTestClient.BodyContentSpec deleteAndVerifyRecommendationsByProductId(int productId, HttpStatus expectedStatus) {
//        return client.delete()
//                .uri("/recommendation?productId=" + productId)
//                .accept(APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus)
//                .expectBody()
//                .consumeWith(result -> {
//                    System.out.println("============Req/Rsp============");
//                    System.out.println(result.toString());
//                    System.out.println("================================");
//                });
//    }

    // 7장 비동기
//    @Autowired
//    private WebTestClient client;
//
//    @Autowired
//    private RecommendationRepository repository;
//
////    @Autowired
////    private Sink channels;
//
//    private AbstractMessageChannel input = null;
//
//    @Before
//    public void setupDb() {
//        input = (AbstractMessageChannel) channels.input();
//        repository.deleteAll().block();
//    }
//
//    @Test
//    public void getRecommendationsByProductId() {
//
//        int productId = 1;
//
//        sendCreateRecommendationEvent(productId, 1);
//        sendCreateRecommendationEvent(productId, 2);
//        sendCreateRecommendationEvent(productId, 3);
//
//        assertEquals(3, (long)repository.findByProductId(productId).count().block());
//
//        getAndVerifyRecommendationsByProductId(productId, OK)
//                .jsonPath("$.length()").isEqualTo(3)
//                .jsonPath("$[2].productId").isEqualTo(productId)
//                .jsonPath("$[2].recommendationId").isEqualTo(3);
//    }
//
//    @Test
//    public void duplicateError() {
//
//        int productId = 1;
//        int recommendationId = 1;
//
//        sendCreateRecommendationEvent(productId, recommendationId);
//
//        assertEquals(1, (long)repository.count().block());
//
//        try {
//            sendCreateRecommendationEvent(productId, recommendationId);
//            fail("Expected a MessagingException here!");
//        } catch (MessagingException me) {
//            if (me.getCause() instanceof InvalidInputException)	{
//                InvalidInputException iie = (InvalidInputException)me.getCause();
//                assertEquals("Duplicate key, Product Id: 1, Recommendation Id:1", iie.getMessage());
//            } else {
//                fail("Expected a InvalidInputException as the root cause!");
//            }
//        }
//
//        assertEquals(1, (long)repository.count().block());
//    }
//
//    @Test
//    public void deleteRecommendations() {
//
//        int productId = 1;
//        int recommendationId = 1;
//
//        sendCreateRecommendationEvent(productId, recommendationId);
//        assertEquals(1, (long)repository.findByProductId(productId).count().block());
//
//        sendDeleteRecommendationEvent(productId);
//        assertEquals(0, (long)repository.findByProductId(productId).count().block());
//
//        sendDeleteRecommendationEvent(productId);
//    }
//
//    @Test
//    public void getRecommendationsMissingParameter() {
//
//        getAndVerifyRecommendationsByProductId("", BAD_REQUEST)
//                .jsonPath("$.path").isEqualTo("/recommendation")
//                .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
//    }
//
//    @Test
//    public void getRecommendationsInvalidParameter() {
//
//        getAndVerifyRecommendationsByProductId("?productId=no-integer", BAD_REQUEST)
//                .jsonPath("$.path").isEqualTo("/recommendation")
//                .jsonPath("$.message").isEqualTo("Type mismatch.");
//    }
//
//    @Test
//    public void getRecommendationsNotFound() {
//
//        getAndVerifyRecommendationsByProductId("?productId=113", OK)
//                .jsonPath("$.length()").isEqualTo(0);
//    }
//
//    @Test
//    public void getRecommendationsInvalidParameterNegativeValue() {
//
//        int productIdInvalid = -1;
//
//        getAndVerifyRecommendationsByProductId("?productId=" + productIdInvalid, UNPROCESSABLE_ENTITY)
//                .jsonPath("$.path").isEqualTo("/recommendation")
//                .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
//    }
//
//    private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(int productId, HttpStatus expectedStatus) {
//        return getAndVerifyRecommendationsByProductId("?productId=" + productId, expectedStatus);
//    }
//
//    private WebTestClient.BodyContentSpec getAndVerifyRecommendationsByProductId(String productIdQuery, HttpStatus expectedStatus) {
//        return client.get()
//                .uri("/recommendation" + productIdQuery)
//                .accept(APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus)
//                .expectHeader().contentType(APPLICATION_JSON)
//                .expectBody();
//    }
//
//    private void sendCreateRecommendationEvent(int productId, int recommendationId) {
//        Recommendation recommendation = new Recommendation(productId, recommendationId, "Author " + recommendationId, recommendationId, "Content " + recommendationId, "SA");
//        Event<Integer, Product> event = new Event(CREATE, productId, recommendation);
//        input.send(new GenericMessage<>(event));
//    }
//
//    private void sendDeleteRecommendationEvent(int productId) {
//        Event<Integer, Product> event = new Event(DELETE, productId, null);
//        input.send(new GenericMessage<>(event));
//    }
}
