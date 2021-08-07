package se.yg.microservices.core.recommendation.repositories;

import org.springframework.data.repository.CrudRepository;
import se.yg.microservices.core.recommendation.model.RecommendationEntity;

import java.util.List;

public interface RecommendationRepository extends CrudRepository<RecommendationEntity, String> {
    List<RecommendationEntity> findByProductId(int productId);
}
