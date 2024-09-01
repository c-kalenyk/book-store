package mate.academy.bookstore.service;

import java.util.Set;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;

public interface OrderService {
    OrderDto create(Long id, CreateOrderRequestDto requestDto);

    Set<OrderDto> getUserOrders(Long userId);

    OrderDto updateOrderStatus(Long id, UpdateOrderRequestDto requestDto);
}
