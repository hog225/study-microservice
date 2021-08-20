package se.yg.microservices.core.review.services;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.yg.api.core.review.Review;
import se.yg.api.core.review.ReviewService;
import se.yg.api.event.Event;
import se.yg.util.exceptions.EventProcessingException;

@Log4j2
@EnableBinding(Sink.class)
public class MessageProcessor {

    private final ReviewService reviewService;

    @Autowired
    public MessageProcessor(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Review> event) {

        log.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Review review = event.getData();
                log.info("Create review with ID: {}/{}", review.getProductId(), review.getReviewId());
                reviewService.createReview(review);
                break;

            case DELETE:
                int productId = event.getKey();
                log.info("Delete reviews with ProductID: {}", productId);
                reviewService.deleteReviews(productId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                log.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        log.info("Message processing done!");
    }
}