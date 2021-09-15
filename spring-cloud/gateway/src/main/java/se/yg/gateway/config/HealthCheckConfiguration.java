package se.yg.gateway.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import reactor.core.publisher.Mono;


@Configuration
public class HealthCheckConfiguration {
//
//    @Bean
//    ReactiveHealthIndicator healthCheckMicroservices(){
//
//    }
//
//    private Mono<Health> getHealth(String url){
//        url += "/actuator/health";
//
//
//    }
}
