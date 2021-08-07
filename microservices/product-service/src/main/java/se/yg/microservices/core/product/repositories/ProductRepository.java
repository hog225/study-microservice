package se.yg.microservices.core.product.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import se.yg.microservices.core.product.model.ProductEntity;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    Optional<ProductEntity> findByProductId(int productId); // 0개 혹은 1개
}
