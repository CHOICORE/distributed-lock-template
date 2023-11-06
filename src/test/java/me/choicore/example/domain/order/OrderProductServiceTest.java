package me.choicore.example.domain.order;

import me.choicore.example.ProductTestDataLoader;
import me.choicore.example.domain.exception.ProductNotFoundException;
import me.choicore.example.domain.order.command.OrderForm;
import me.choicore.example.domain.order.command.OrderProduct;
import me.choicore.example.domain.product.ProductRepository;
import me.choicore.example.domain.product.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ProductTestDataLoader.class})
class OrderProductServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private OrderProductService orderProductService;

    @Autowired
    private ProductRepository productRepository;


    @Test
    @DisplayName("유효하지 않은 상품 아이디로 주문을 진행하면 예외가 발생한다.")
    void t1() {

        // given
        Pair<Long, Integer> product1 = Pair.of(1L, 100);
        Pair<Long, Integer> product2 = Pair.of(999L, 200);

        var orderProducts = List.of(
                OrderProduct.of(product1.getFirst(), 20)
                , OrderProduct.of(product2.getFirst(), 20)
        );

        // then
        Assertions.assertThatThrownBy(() -> {

            // when
            orderProductService.orderProcessing(OrderForm.of(orderProducts));
        }).isInstanceOf(ProductNotFoundException.class);

        // then
        Product product = productRepository.findById(product1.getFirst()).get();
        Assertions.assertThat(product.getStock()).isEqualTo(product1.getSecond());
    }

    @Test
    @DisplayName("주문을 등록하는 과정에서 장애가 발생하면 재고를 복구한다.")
    void t2() {
        Pair<Long, Integer> product1 = Pair.of(1L, 100);
        Pair<Long, Integer> product2 = Pair.of(2L, 200);

        var orderProducts = List.of(
                OrderProduct.of(product1.getFirst(), 20)
                , OrderProduct.of(product2.getFirst(), 20)
        );
        Assertions.assertThatThrownBy(() -> {
            orderProductService.orderProcessing(OrderForm.of(orderProducts));
        }).isInstanceOf(RuntimeException.class);

        Product foundProduct1 = productRepository.findById(product1.getFirst()).get();
        Assertions.assertThat(foundProduct1.getStock()).isEqualTo(product1.getSecond());

        Product foundProduct2 = productRepository.findById(product2.getFirst()).get();
        Assertions.assertThat(foundProduct2.getStock()).isEqualTo(product2.getSecond());
    }
}