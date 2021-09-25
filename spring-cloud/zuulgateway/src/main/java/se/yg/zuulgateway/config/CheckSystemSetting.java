package se.yg.zuulgateway.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
@Log4j2
public class CheckSystemSetting {
//
//    @Value("${server.port}")
//    String port;
//
//    @Value("${zuul.routes}")
//    String ZuulVal;

    @Autowired
    private Environment env;

    @Bean
    public void printApplicationYML(){
        log.info(env.getProperty("zuul.routes.product-composite.url"));
        log.info(env.getProperty("zuul.routes.product-composite.serviceId"));
        log.info(env.getProperty("app.eureka-server"));

    }
}
