package se.yg.microservices.core.review.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import se.yg.api.core.review.Review;
import se.yg.api.core.review.ReviewService;
import se.yg.microservices.core.review.model.ReviewEntity;
import se.yg.microservices.core.review.repositories.ReviewRepository;
import se.yg.util.exceptions.InvalidInputException;
import se.yg.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repository;

    private final ReviewMapper mapper;

    private final ServiceUtil serviceUtil;

    private final Scheduler scheduler;

    @Autowired
    public ReviewServiceImpl(ReviewRepository repository, ReviewMapper mapper, ServiceUtil serviceUtil, Scheduler scheduler) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
        this.scheduler = scheduler;
    }

    //블로킹
////    @Override
////    public List<Review> getReviews(int productId) {
////
////        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
////
////        if (productId == 213) {
////            LOG.debug("No reviews found for productId: {}", productId);
////            return  new ArrayList<>();
////        }
////
////        List<Review> list = new ArrayList<>();
////        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
////        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
////        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));
////
////        LOG.debug("/reviews response size: {}", list.size());
////
////        return list;
////    }
//
//    @Override
//    public Review createReview(Review body) {
//        try {
//            ReviewEntity entity = mapper.apiToEntity(body);
//            ReviewEntity newEntity = repository.save(entity);
//
//            LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
//            return mapper.entityToApi(newEntity);
//
//        } catch (DataIntegrityViolationException dive) {
//            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
//        }
//    }
//
//    @Override
//    public List<Review> getReviews(int productId) {
//
//        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
//
//        List<ReviewEntity> entityList = repository.findByProductId(productId);
//        List<Review> list = mapper.entityListToApiList(entityList);
//        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
//
//        LOG.debug("getReviews: response size: {}", list.size());
//
//        return list;
//    }
//
//    @Override
//    public void deleteReviews(int productId) {
//        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
//        repository.deleteAll(repository.findByProductId(productId));
//    }

    @Override
    public Review createReview(Review body) {
        try {
            ReviewEntity entity = mapper.apiToEntity(body);
            ReviewEntity newEntity = repository.save(entity);

            LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    @Override
    public Flux<Review> getReviews(int productId){
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        return asyncFlux(getByProductId(productId)).log();
    }


    protected List<Review> getByProductId(int productId) {


        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getReviews: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteReviews(int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }

    private <T> Flux<T> asyncFlux(Iterable<T> iterable){
        return Flux.fromIterable(iterable).publishOn(scheduler);
    }
}
