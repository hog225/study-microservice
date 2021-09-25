package se.yg.zuulgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import se.yg.zuulgateway.filters.post.PostFilter;
import se.yg.zuulgateway.filters.pre.SimpleFilter;

@EnableZuulProxy
@SpringBootApplication
public class ZuulgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulgatewayApplication.class, args);
	}

	@Bean
	public SimpleFilter simpleFilter(){
		return new SimpleFilter();
	}

	@Bean
	public PostFilter postFilter(){
		return new PostFilter();
	}
}
