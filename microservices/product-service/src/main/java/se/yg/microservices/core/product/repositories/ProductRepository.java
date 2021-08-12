package se.yg.microservices.core.product.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import se.yg.microservices.core.product.model.ProductEntity;

import java.util.Optional;

public interface ProductRepository extends ReactiveMongoRepository<ProductEntity, String> {
    Mono<ProductEntity> findByProductId(int productId); // 0개 혹은 1개
}
