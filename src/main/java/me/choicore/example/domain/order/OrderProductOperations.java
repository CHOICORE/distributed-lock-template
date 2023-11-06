package me.choicore.example.domain.order;

import me.choicore.example.domain.order.command.OrderProduct;
import me.choicore.example.domain.product.entity.Product;

import java.util.List;

public interface OrderProductOperations {

    void consumeStock(Long productId, int quantity);

    List<Product> consumeStock(List<OrderProduct> orderProducts);

    List<Product> restock(List<OrderProduct> orderProducts);
}
