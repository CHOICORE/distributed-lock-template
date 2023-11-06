package me.choicore.example.domain.order.command;

import me.choicore.example.domain.product.entity.Product;

import java.util.List;


public record OrderSheet(
        List<Product> products
) {
}
