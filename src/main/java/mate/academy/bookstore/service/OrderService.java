package mate.academy.bookstore.service;

import java.util.Set;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto create(Long id, CreateOrderRequestDto requestDto);

    Set<OrderDto> getUserOrders(Long userId, Pageable pageable);

    OrderDto updateOrderStatus(Long id, UpdateOrderRequestDto requestDto);

    OrderItemDto getSpecificItem(Long userId, Long orderId, Long itemId);

    Set<OrderItemDto> getItemsFromOrder(Long userId, Long orderId);
}
