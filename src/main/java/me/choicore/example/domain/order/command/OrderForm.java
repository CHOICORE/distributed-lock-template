package me.choicore.example.domain.order.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor(staticName = "of")
public class OrderForm {

    private List<OrderProduct> orderProducts = new ArrayList<>();
}
