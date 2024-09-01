package mate.academy.bookstore.service;

import java.util.Set;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;

public interface OrderItemService {
    OrderItemDto getByOrderIdAndItemId(Long orderId, Long itemId);

    Set<OrderItemDto> getItemsFromOrder(Long orderId);
}
