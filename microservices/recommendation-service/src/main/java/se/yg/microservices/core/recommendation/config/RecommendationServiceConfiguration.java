package se.yg.microservices.core.recommendation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import se.yg.microservices.core.recommendation.model.RecommendationEntity;

@Configuration
public class RecommendationServiceConfiguration {
    // Not Reactive
//    @Autowired
//    MongoOperations mongoTemplate;
//
//    @EventListener(ContextRefreshedEvent.class)
//    public void initIndicesAfterStartup() {
//
//        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
//        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
//
//        IndexOperations indexOps = mongoTemplate.indexOps(RecommendationEntity.class);
//        resolver.resolveIndexFor(RecommendationEntity.class).forEach(e -> indexOps.ensureIndex(e));
//    }

    @Autowired
    ReactiveMongoOperations mongoTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        ReactiveIndexOperations indexOps = mongoTemplate.indexOps(RecommendationEntity.class);
        resolver.resolveIndexFor(RecommendationEntity.class).forEach(e -> indexOps.ensureIndex(e).block());
    }
}
