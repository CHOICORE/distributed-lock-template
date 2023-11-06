package me.choicore.example.domain.product;

import me.choicore.example.domain.product.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findByIdIn(Set<Long> productIds);
}
