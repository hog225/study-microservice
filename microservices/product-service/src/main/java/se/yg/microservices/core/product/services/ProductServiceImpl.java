package se.yg.microservices.core.product.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.yg.api.core.product.Product;
import se.yg.api.core.product.ProductService;
import se.yg.microservices.core.product.model.ProductEntity;
import se.yg.microservices.core.product.repositories.ProductRepository;
import se.yg.util.exceptions.InvalidInputException;
import se.yg.util.exceptions.NotFoundException;
import se.yg.util.http.ServiceUtil;

import java.util.Random;

import static reactor.core.publisher.Mono.error;

@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;


    @Autowired
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil){
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Product> getProduct(int productId, int delay, int faultPercent) {
        LOG.debug("/product return the found product for productId={}", productId);

        // resilience4j 를 위해 일부로 ....
        if(delay > 0)
            simulateDelay(delay);
        if(faultPercent > 0)
            throwErrorIfBadLuck(faultPercent);

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
        // 블로킹
        // orElseThrow 는 Optional 의 기능
//        ProductEntity entity = repository.findByProductId(productId).orElseThrow(()->
//            new NotFoundException("No Product ID Founded: " + productId));
//        Product response = mapper.entityToApi(entity);
//        response.setServiceAddress(serviceUtil.getServiceAddress());

        // 영속성 없었을때 테스트를 위해
        //if (productId == 13) throw new NotFoundException("No product found for productId: " + productId);
        //return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());

        //return response;

        return repository.findByProductId(productId)
                .switchIfEmpty(error(new NotFoundException("No Product ID Founded: " + productId)))
                .log()
                .map(e->mapper.entityToApi(e))
                .map(e->{
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
    }

    //Blocking
//    @Override
//    public Product createProduct(Product body) {
//        try {
//            ProductEntity entity = mapper.apiToEntity(body);
//            ProductEntity newEntity = repository.save(entity);
//
//            LOG.debug("createProduct: entity created for productId: {}", body.getProductId());
//            return mapper.entityToApi(newEntity);
//
//        } catch (DuplicateKeyException dke) {
//            throw new InvalidInputException("Duplicate key, Product ID : " + body.getProductId());
//        }
//    }

    @Override
    public Product createProduct(Product body){

        if (body.getProductId() < 1) throw new InvalidInputException("Invalid product id: " + body.getProductId());
        ProductEntity entity = mapper.apiToEntity(body);
        Mono<Product> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicated key, Product ID : " + body.getProductId())
                ).map(e -> mapper.entityToApi(e));

        //return mapper.entityToApi(newEntity);
        return newEntity.block();
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        //repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
        repository.findByProductId(productId).log()
                .map(e->repository.delete(e))
                .flatMap(e->e).block();
    }

    private void simulateDelay(int delay) {
        LOG.debug("Sleeping for {} seconds...", delay);
        try {Thread.sleep(delay * 1000);} catch (InterruptedException e) {}
        LOG.debug("Moving on...");
    }

    private void throwErrorIfBadLuck(int faultPercent) {
        int randomThreshold = getRandomNumber(1, 100);
        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.debug("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }
    }


    private final Random randomNumberGenerator = new Random();
    private int getRandomNumber(int min, int max) {

        if (max < min) {
            throw new RuntimeException("Max must be greater than min");
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }
}