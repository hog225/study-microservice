package se.yg.microservices.composite.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.yg.api.core.product.Product;
import se.yg.api.core.product.ProductService;
import se.yg.api.core.recommendation.Recommendation;
import se.yg.api.core.recommendation.RecommendationService;
import se.yg.api.core.review.Review;
import se.yg.api.core.review.ReviewService;
import se.yg.api.event.Event;
import se.yg.util.exceptions.InvalidInputException;
import se.yg.util.exceptions.NotFoundException;
import se.yg.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static reactor.core.publisher.Flux.empty;
import static se.yg.api.event.Event.Type.DELETE;
// 세가지 핵심 서비스의 API 인터페이스 구현

// 이벤트를 다른 토픽에 게시
@EnableBinding(ProductCompositeIntegration.MessageSources.class)
@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private MessageSources messageSources;
    public interface MessageSources {

        String OUTPUT_PRODUCTS = "output-products";
        String OUTPUT_RECOMMENDATIONS = "output-recommendations";
        String OUTPUT_REVIEWS = "output-reviews";

        @Output(OUTPUT_PRODUCTS)
        MessageChannel outputProducts();

        @Output(OUTPUT_RECOMMENDATIONS)
        MessageChannel outputRecommendations();

        @Output(OUTPUT_REVIEWS)
        MessageChannel outputReviews();
    }
    private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            WebClient.Builder webClient,
            MessageSources messageSources,
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int    productServicePort,

            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int    recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int    reviewServicePort
    ) {

        this.restTemplate = restTemplate; // main 에서 Bean 으로 등록 되어 있음
        this.mapper = mapper;
        this.webClient = webClient.build();
        this.messageSources = messageSources;

        productServiceUrl        = "http://" + productServiceHost + ":" + productServicePort + "/product";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        reviewServiceUrl         = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
    }

    @Override
    public Product createProduct(Product body) {

        try {
            String url = productServiceUrl;
            LOG.debug("Will post a new product to URL: {}", url);

            Product product = restTemplate.postForObject(url, body, Product.class);
            LOG.debug("Created a product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

//    @Override
//    public Product getProduct(int productId) {
//
//
//        try {
//            String url = productServiceUrl + "/" + productId;
//            LOG.debug("Will call the getProduct API on URL: {}", url);
//
//            Product product = restTemplate.getForObject(url, Product.class);
//            LOG.debug("Found a product with id: {}", product.getProductId());
//
//            return product;
//
//        } catch (HttpClientErrorException ex) {
//            throw handleHttpClientException(ex);
//        }
//    }

    @Override
    public Mono<Product> getProduct(int productId) {



        String url = productServiceUrl + "/" + productId;
        LOG.debug("Will call the getProduct API on URL: {}", url);

        //논블로킹
        return webClient.get().uri(url).retrieve().bodyToMono(Product.class).log().onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }


    //동기
//    @Override
//    public void deleteProduct(int productId) {
//        try {
//            String url = productServiceUrl + "/" + productId;
//            LOG.debug("Will call the deleteProduct API on URL: {}", url);
//
//            restTemplate.delete(url);
//
//        } catch (HttpClientErrorException ex) {
//            throw handleHttpClientException(ex);
//        }
//    }
    @Override
    public void deleteProduct(int productId){
        messageSources.outputProducts().send(MessageBuilder.withPayload(new Event(DELETE, productId, null)).build());
    }
    @Override
    public Recommendation createRecommendation(Recommendation body) {

        try {
            String url = recommendationServiceUrl;
            LOG.debug("Will post a new recommendation to URL: {}", url);

            Recommendation recommendation = restTemplate.postForObject(url, body, Recommendation.class);
            LOG.debug("Created a recommendation with id: {}", recommendation.getProductId());

            return recommendation;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

//    @Override
//    public List<Recommendation> getRecommendations(int productId) {
//
//        try {
//            String url = recommendationServiceUrl + "?productId=" + productId;
//
//            LOG.debug("Will call the getRecommendations API on URL: {}", url);
//            List<Recommendation> recommendations = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {}).getBody();
//
//            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
//            return recommendations;
//
//        } catch (Exception ex) {
//            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
//            return new ArrayList<>();
//        }
//    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId){
        String url = recommendationServiceUrl + "?productId=" + productId;
        return webClient.get().uri(url).retrieve().bodyToFlux(Recommendation.class).log().onErrorResume(error->empty());

    }


    @Override
    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Review createReview(Review body) {

        try {
            String url = reviewServiceUrl;
            LOG.debug("Will post a new review to URL: {}", url);

            Review review = restTemplate.postForObject(url, body, Review.class);
            LOG.debug("Created a review with id: {}", review.getProductId());

            return review;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

//    @Override
//    public List<Review> getReviews(int productId) {
//
//        try {
//            String url = reviewServiceUrl + "?productId=" + productId;
//
//            LOG.debug("Will call the getReviews API on URL: {}", url);
//            List<Review> reviews = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {}).getBody();
//
//            LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
//            return reviews;
//
//        } catch (Exception ex) {
//            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
//            return new ArrayList<>();
//        }
//    }
    @Override
    public Flux<Review> getReviews(int productId){
        String url = reviewServiceUrl + "?productId=" + productId;
        return webClient.get().uri(url).retrieve().bodyToFlux(Review.class).onErrorResume(error->empty());
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteReviews API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }


    // 블로킹
    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }


}
