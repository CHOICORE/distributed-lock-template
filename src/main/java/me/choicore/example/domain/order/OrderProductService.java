package me.choicore.example.domain.order;

import lombok.RequiredArgsConstructor;
import me.choicore.example.domain.exception.OrderProcessingException;
import me.choicore.example.domain.order.command.OrderForm;
import me.choicore.example.domain.order.command.OrderSheet;
import me.choicore.example.domain.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductOperations orderProductOperations;

    public void orderProcessing(OrderForm orderForm) {

        List<Product> products = orderProductOperations.consumeStock(orderForm.getOrderProducts());

        try {
            OrderSheet orderSheet = new OrderSheet(products);
            // ...
            throw new OrderProcessingException("주문 처리 중 오류가 발생했습니다. 주문을 취소합니다.");
        } catch (RuntimeException e) {
            orderProductOperations.restock(orderForm.getOrderProducts());
            throw e;
        }
    }
}
