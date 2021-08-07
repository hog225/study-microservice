package se.yg.microservices.core.review.repositories;

import org.springframework.data.repository.CrudRepository;
import se.yg.microservices.core.review.model.ReviewEntity;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
    @Transactional(readOnly = true)
    List<ReviewEntity> findByProductId(int productId);
}
