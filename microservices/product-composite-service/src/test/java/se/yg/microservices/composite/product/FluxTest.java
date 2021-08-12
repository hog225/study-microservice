package se.yg.microservices.composite.product;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import se.yg.util.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class FluxTest {

    @Test
    public void testFlux(){
        List<Integer> list = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
                .filter(n -> n%2 == 0)
                .map(n -> n*2)
                .log()
                .subscribe(n -> list.add(n));
                //.assertThat(list).containsExactly(4, 8);
    }

    @Test
    public void TestFluxBlocking() {

        List<Integer> list = Flux.just(1, 2, 3, 4)
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .log()
                .collectList().block();
    }


    @Test
    public void TestFluxBlock2() {

        //Function<? super Throwable, ? extends Publisher<? extends T>> function = ;
        //Function<? super Throwable, ? extends Publisher<? extends T>> fallback
        List<Integer> list = Flux.just(1, 2, 3, 4, 5, 6)
                .map(n -> {
                    if (n == 3){
                        throw new RuntimeException("Num is " + n);
                    }
                    return n;
                })
                .onErrorResume(e-> {
                    System.out.println(e);
                    return true;
                }, t->{
                    return Flux.just(7,8,9,10);
                })
                .log()
                .collectList().block();
    }
}
