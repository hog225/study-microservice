package se.yg.microservices.composite.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false"})
public class SimpleTest {

    @Value("${server.port}")           String apiVersion;

    @Test
    public void printProperty(){
        System.out.println(apiVersion);
    }
}
