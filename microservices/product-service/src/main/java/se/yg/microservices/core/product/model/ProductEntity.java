package se.yg.microservices.core.product.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "products")
public class ProductEntity {
    @Id
    private String Id;

    @Version
    private Integer version;

    //productID에 생성된 고유 색인을 가져 온다.
    @Indexed(unique = true)
    private int productId;

    private String name;
    private int weight;


    public ProductEntity(int productId, String name, int weight) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
    }



}
