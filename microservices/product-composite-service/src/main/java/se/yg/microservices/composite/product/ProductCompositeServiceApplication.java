package se.yg.microservices.composite.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import se.yg.microservices.composite.product.service.ProductCompositeIntegration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.LinkedHashMap;


@EnableSwagger2
@SpringBootApplication
@ComponentScan("se.yg")
public class ProductCompositeServiceApplication {

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

//	@Autowired
//	ProductCompositeIntegration integration;
//
//	@Bean
//	ReactiveHealthIndicator coreService(){
//
//	}


	public static void main(String[] args) {
		SpringApplication.run(ProductCompositeServiceApplication.class, args);
	}

}
