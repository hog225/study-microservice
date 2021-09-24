package se.yg.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
public class HealthCheckConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);

    //https://www.baeldung.com/spring-boot-actuators
//
//    @Bean
//    ReactiveHealthIndicator healthCheckMicroservices(){
//
//    }
//
    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    @Autowired
    public HealthCheckConfiguration(
            WebClient.Builder webClientBuilder

    ) {
        this.webClientBuilder = webClientBuilder;
    }



    private WebClient getWebClient() {

        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }

    private Mono<Health> getHealth(String url){
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL {}", url);
        return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
                .map(s-> new Health.Builder().up().build())
                .onErrorResume(ex ->
                        Mono.just(new Health.Builder().down(ex).build())).log();
    }
}
