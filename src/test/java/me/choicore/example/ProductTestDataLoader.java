package me.choicore.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import me.choicore.example.domain.product.ProductRepository;
import me.choicore.example.domain.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@TestConfiguration(proxyBeanMethods = false)
public class ProductTestDataLoader {
    private static final Logger log = LoggerFactory.getLogger(ProductTestDataLoader.class);

    @Autowired
    private ProductRepository productRepository;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {

            ObjectMapper objectMapper = new ObjectMapper();

            String location = "/data/products.json";

            log.info("Loading data into database from JSON: classpath:/{}", location);

            try {
                InputStream resourceAsStream = getClass().getResourceAsStream(location);
                Product[] products = objectMapper.readValue(resourceAsStream, Product[].class);
                productRepository.saveAll(Arrays.asList(products));
            } catch (IOException e) {
                log.error("Failed to load data from resource: {}", location, e);
            }

        };
    }
}