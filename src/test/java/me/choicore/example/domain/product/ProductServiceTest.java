package me.choicore.example.domain.product;

import me.choicore.example.ProductTestDataLoader;
import me.choicore.example.domain.exception.ProductNotFoundException;
import me.choicore.example.domain.order.command.OrderProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ProductTestDataLoader.class})
class ProductServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private ProductService productService;

    @Test
    void t1() {
        List<OrderProduct> orderProducts = List.of(
                OrderProduct.of(1L, 20)
                , OrderProduct.of(2L, 20)
        );
        productService.consumeStock(orderProducts);
    }

    @Test
    void t2() {
        List<OrderProduct> orderProducts = List.of(
                OrderProduct.of(1L, 20)
                , OrderProduct.of(999L, 20)
        );
        Assertions.assertThatThrownBy(() -> {
            productService.consumeStock(orderProducts);
        }).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void t3() {
        productService.consumeStock(1L, 20);
    }
}