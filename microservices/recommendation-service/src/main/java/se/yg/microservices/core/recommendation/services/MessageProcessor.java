package se.yg.microservices.core.recommendation.services;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.yg.api.core.recommendation.Recommendation;
import se.yg.api.core.recommendation.RecommendationService;
import se.yg.api.event.Event;
import se.yg.util.exceptions.EventProcessingException;

@Log4j2
@EnableBinding(Sink.class)
public class MessageProcessor {

    private final RecommendationService recommendationService;

    @Autowired
    public MessageProcessor(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Recommendation> event) {

        log.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Recommendation recommendation = event.getData();
                log.info("Create recommendation with ID: {}/{}", recommendation.getProductId(), recommendation.getRecommendationId());
                recommendationService.createRecommendation(recommendation);
                break;

            case DELETE:
                int productId = event.getKey();
                log.info("Delete recommendations with ProductID: {}", productId);
                recommendationService.deleteRecommendations(productId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                log.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        log.info("Message processing done!");
    }
}
