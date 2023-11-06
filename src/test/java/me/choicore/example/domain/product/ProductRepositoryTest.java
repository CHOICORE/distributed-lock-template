package me.choicore.example.domain.product;


import me.choicore.example.ProductTestDataLoader;
import me.choicore.example.domain.product.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {ProductTestDataLoader.class})
@DataJpaTest
public class ProductRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private ProductRepository productRepository;

    @Test
    void connectionEstablished() {
        Assertions.assertThat(postgres.isCreated()).isTrue();
        Assertions.assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void save() {
        Product product = Product.of("테스트 상품", 1000, 100);
        productRepository.save(product);
        Assertions.assertThat(productRepository.findAll()).isNotNull().hasSize(4);
    }

    @Test
    void findAll() {
        Assertions.assertThat(productRepository.findAll()).isNotNull().hasSize(3);
    }

    @Test
    void deleteById() {
        productRepository.deleteById(1L);
        Assertions.assertThat(productRepository.findAll()).isNotNull().hasSize(2);
    }
}
