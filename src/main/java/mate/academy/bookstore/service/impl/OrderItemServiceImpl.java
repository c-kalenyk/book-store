package mate.academy.bookstore.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import mate.academy.bookstore.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemDto getByOrderIdAndItemId(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByOrderIdAndId(orderId, itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get order item by order id " + orderId + " and item id " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public Set<OrderItemDto> getItemsFromOrder(Long orderId) {
        Set<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItemMapper.toDtoSet(orderItems);
    }
}
