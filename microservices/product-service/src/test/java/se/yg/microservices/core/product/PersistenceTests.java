package se.yg.microservices.core.product;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import se.yg.microservices.core.product.model.ProductEntity;
import se.yg.microservices.core.product.repositories.ProductRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.ASC;


@DataMongoTest
public class PersistenceTests {
    @Autowired
    private ProductRepository repository;
    private ProductEntity savedEntity;

//    @BeforeEach
//    public void setupDb(){
//        System.out.println("------------------------------------");
//        repository.deleteAll();
//        ProductEntity entity = new ProductEntity(1, "n", 1);
//        savedEntity = repository.save(entity);
//        assertEqualsProduct(entity, savedEntity);
//
////        Iterable<ProductEntity> it = repository.findAll();
////        it.forEach(t->System.out.println(t.getName()));
//
//    }
    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        ProductEntity entity = new ProductEntity(1, "n", 1);
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return areProductEqual(entity, savedEntity);
                })
                .verifyComplete();
    }

    // Blocking
//    @Test
//    public void create(){
//        ProductEntity newEntity = new ProductEntity(2, "n", 2);
//        savedEntity = repository.save(newEntity);
//
//        Optional<ProductEntity> foundEntity= repository.findById(newEntity.getId());
//        System.out.println(newEntity.getId() + "    --- "+foundEntity.isPresent());
//
////        Iterable<ProductEntity> it = repository.findAll();
////        it.forEach(t->System.out.println(t.getId() + " + " + newEntity.getId()));
//        //ProductEntity foundEntity = repository.findById(newEntity.getId()).get();
//        assertEqualsProduct(newEntity, foundEntity.get());
//
//        assertEquals(2, repository.count());
//    }

    @Test
    public void create() {
        ProductEntity newEntity = new ProductEntity(2, "n", 2);

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(createdEntity -> newEntity.getProductId() == createdEntity.getProductId())
                .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> areProductEqual(newEntity, foundEntity))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

//    @Test
//    public void update(){
//        System.out.println("update ");
//        savedEntity.setName("n2");
//        repository.save(savedEntity);
//
//        ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();
//
//        assertEquals(1, (long)foundEntity.getVersion());
//        assertEquals("n2", foundEntity.getName());
//
//
//    }

    @Test
    public void update() {
        savedEntity.setName("n2");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2"))
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getName().equals("n2"))
                .verifyComplete();
    }

    @Test
    public void getByProductId() {
        //Optional<ProductEntity> entity = repository.findByProductId(savedEntity.getProductId());
        Mono<ProductEntity> entity = repository.findByProductId(savedEntity.getProductId());
        assertNotNull(entity.block());
        assertEqualsProduct(savedEntity, entity.block());
    }

//    @Test
//    public void delete() {
//        repository.delete(savedEntity);
//        assertFalse(repository.existsById(savedEntity.getId()).block());
//
//        Flux<ProductEntity> listPro = repository.findAll();
//
//        System.out.printf("remain DB Date -------------%d \n ", listPro.collectList().block().size());
//    }
    @Test
    public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }
    // productId 는 Unique Key 이다.
    @Test
    public void duplicateError(){
        assertThrows(DuplicateKeyException.class, ()->{
            ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
            repository.save(entity);
        });
//        ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
//        repository.save(entity);
//        printRepository(repository);
    }

//    @Test
//    public void optimisticLockError(){
//
//        // 동일한 Entity
//        ProductEntity en1 = repository.findById(savedEntity.getId()).block();
//        ProductEntity en2 = repository.findById(savedEntity.getId()).block();
//
//        en1.setName("fe");
//        repository.save(en1);
//
//        assertThrows(OptimisticLockingFailureException.class, ()->{
//            //동일한 엔티티 에 데이터를 변경하려 하지만 엔티티의 버전이 낮아서 실패한다.
//            // 낙관적 잠금 에러가 발생 한다.
//            en2.setName("n2");
//            repository.save(en2);
//        });
//
//        ProductEntity updateEn = repository.findById(savedEntity.getId()).block();
//        assertEquals(1, (int)updateEn.getVersion());
//        assertEquals("fe", updateEn.getName());
//
//
//
//    }

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        ProductEntity entity1 = repository.findById(savedEntity.getId()).block();
        ProductEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getName().equals("n1"))
                .verifyComplete();
    }

//    // PAGE TEST
//    @Test
//    public void paging(){
//        repository.deleteAll();
//        List<ProductEntity> newProducts = rangeClosed(1001, 1010)
//                .mapToObj(i-> new ProductEntity(i, "name "+i, i))
//                .collect(Collectors.toList());
//        repository.saveAll(newProducts);
//
//        Pageable nextPage = PageRequest.of(0, 4, ASC, "productId");
//        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
//        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
//        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
//
//    }
//    private Pageable testNextPage(Pageable nextPage, String expectedProductIds, boolean expectsNextPage) {
//        Page<ProductEntity> productPage = repository.findAll(nextPage);
//        assertEquals(expectedProductIds, productPage.getContent().stream().map(p -> p.getProductId()).collect(Collectors.toList()).toString());
//        assertEquals(expectsNextPage, productPage.hasNext());
//        return productPage.nextPageable();
//    }


    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(),        actualEntity.getProductId());
        assertEquals(expectedEntity.getName(),           actualEntity.getName());
        assertEquals(expectedEntity.getWeight(),           actualEntity.getWeight());
    }

    private void printRepository(ProductRepository productRepository){
        List<ProductEntity> it = productRepository.findAll().collectList().block();
        it.forEach(t->System.out.println("+++" + t.getId() + "--" + t.getVersion() + "--" + t.getName() + "--" +t.getProductId()));

    }

    private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
        return
                (expectedEntity.getId().equals(actualEntity.getId())) &&
                        (expectedEntity.getVersion() == actualEntity.getVersion()) &&
                        (expectedEntity.getProductId() == actualEntity.getProductId()) &&
                        (expectedEntity.getName().equals(actualEntity.getName())) &&
                        (expectedEntity.getWeight() == actualEntity.getWeight());
    }
}
