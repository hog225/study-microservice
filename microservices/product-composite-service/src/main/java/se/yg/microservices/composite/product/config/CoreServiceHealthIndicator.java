package se.yg.microservices.composite.product.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import se.yg.microservices.composite.product.service.ProductCompositeIntegration;

@Component
@Log4j2
public class CoreServiceHealthIndicator implements ReactiveHealthIndicator {

    @Autowired
    ProductCompositeIntegration productCompositeIntegration;

    @Override
    public Mono<Health> health() {

        return Mono.just(
                new Health.Builder()
                        .withDetail("product", productCompositeIntegration.getProductHealth())
                        .withDetail("recommendation", productCompositeIntegration.getRecommendationHealth())
                        .withDetail("review", productCompositeIntegration.getReviewHealth())
                        .build());

    }

}
