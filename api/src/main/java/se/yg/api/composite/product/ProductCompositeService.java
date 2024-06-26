package se.yg.api.composite.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Api("REST API for composite product information")
public interface ProductCompositeService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/product-composite \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"name":"product 123","weight":123}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.product-composite.create-composite-product.description}",
            notes = "${api.product-composite.create-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PostMapping(
            value    = "/product-composite",
            consumes = "application/json")
    //비동기 생성 이벤트를 토픽 (Prodcut, Recommendations, Reviews) 에 개시 후 호출자에게 바로 OK 를 준다.
    void createCompositeProduct(@RequestBody ProductAggregate body);

    /**
     * Sample usage: curl $HOST:$PORT/product-composite/1
     *
     * @param productId
     * @return the composite product info, if found, else null
     */
    @ApiOperation(
            value = "${api.product-composite.get-composite-product.description}",
            notes = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
            value    = "/product-composite/{productId}",
            produces = "application/json")
    //동기 API
    Mono<ProductAggregate> getCompositeProduct(
            @PathVariable int productId,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent
    );


    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/product-composite/1
     *
     * @param productId
     */
    @ApiOperation(
            value = "${api.product-composite.delete-composite-product.description}",
            notes = "${api.product-composite.delete-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @DeleteMapping(value = "/product-composite/{productId}")
    //비동기 삭제 이벤트를 토픽 (Prodcut, Recommendations, Reviews) 에 개시 후 호출자에게 바로 OK 를 준다.
    void deleteCompositeProduct(@PathVariable int productId);
}
