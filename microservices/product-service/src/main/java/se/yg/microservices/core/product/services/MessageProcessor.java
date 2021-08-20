package se.yg.microservices.core.product.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.yg.api.core.product.Product;
import se.yg.api.core.product.ProductService;
import se.yg.api.event.Event;
import se.yg.util.exceptions.EventProcessingException;

@Log4j2
@EnableBinding(Sink.class)
public class MessageProcessor {
    private ProductService productService;

    @Autowired
    public MessageProcessor(ProductService productService){
        this.productService = productService;
    }

    @StreamListener(target = Sink.INPUT) //수신 채널을 지정하고 메시지를 소비하고 처리하는 메서드에 붙이는 애노테이션
    public void process(Event<Integer, Product> event){

        log.info("Process Message created at {} ....", event.getEventCreatedAt());
        switch (event.getEventType()){
            case CREATE:
                Product product = event.getData();
                log.info("Create product with ID: {}", product.getProductId());
                productService.createProduct(product);
                break;

            case DELETE:
                int productId = event.getKey();
                log.info("Delete product with ProductID: {}", productId);
                productService.deleteProduct(productId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                log.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }
        log.info("Message processing done!");
    }
}
