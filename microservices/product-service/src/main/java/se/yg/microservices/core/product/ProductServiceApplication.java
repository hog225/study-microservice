package se.yg.microservices.core.product;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("se.yg")
@Log4j2
public class ProductServiceApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(ProductServiceApplication.class, args);
		String mongoDBHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongoDBPort = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		log.info("Connected to mongoDB: " + mongoDBHost + ":" + mongoDBPort);
	}

}
