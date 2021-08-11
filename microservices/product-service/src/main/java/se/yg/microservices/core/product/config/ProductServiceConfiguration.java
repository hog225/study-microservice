package se.yg.microservices.core.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
import org.springframework.web.client.RestTemplate;
import se.yg.microservices.core.product.model.ProductEntity;

@Configuration
public class ProductServiceConfiguration {

    //@Autowired
//    private final MongoOperations mongoTemplate;



//    @Autowired
//    ProductServiceConfiguration(MongoOperations mongoTemplate){
//        this.mongoTemplate = mongoTemplate;
//    }


//    @EventListener(ContextRefreshedEvent.class)
//    public void initIndicesAfterStartup() {
//
//        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
//        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
//
//        IndexOperations indexOps = mongoTemplate.indexOps(ProductEntity.class);
//        resolver.resolveIndexFor(ProductEntity.class).forEach(e -> indexOps.ensureIndex(e));
//    }

    @Autowired
    ReactiveMongoOperations mongoTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        ReactiveIndexOperations indexOps = mongoTemplate.indexOps(ProductEntity.class);
        resolver.resolveIndexFor(ProductEntity.class).forEach(e -> indexOps.ensureIndex(e).block());
    }
}
