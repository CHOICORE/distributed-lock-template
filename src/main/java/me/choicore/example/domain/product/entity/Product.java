package me.choicore.example.domain.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import me.choicore.example.domain.exception.OutOfStockException;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;

    @Builder
    public Product(
            final String name,
            final BigDecimal price,
            final int stock
    ) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static Product of(
            final String name,
            final long price,
            final int stock
    ) {
        return Product.builder()
                .name(name)
                .price(new BigDecimal(price))
                .stock(stock)
                .build();
    }

    public static Product of(
            final String name,
            final double price,
            final int stock
    ) {
        return Product.builder()
                .name(name)
                .price(new BigDecimal(price))
                .stock(stock)
                .build();
    }

    public void decreaseStock(final int quantity) {
        if (outOfStock() || this.stock < quantity) {
            throw new OutOfStockException("재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

    public void increaseStock(final int quantity) {
        this.stock += quantity;
    }

    public boolean outOfStock() {
        return this.stock < 1;
    }

    public void restock(int quantity) {
        this.stock = quantity;
    }

}
