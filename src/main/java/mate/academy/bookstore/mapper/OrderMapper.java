package mate.academy.bookstore.mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderDate", dateFormat = "yyyy-MM-dd HH:mm")
    OrderDto toDto(Order order);

    Set<OrderDto> toDtoSet(Collection<Order> orders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "total", source = "shoppingCart.cartItems", qualifiedByName = "total")
    @Mapping(target = "orderItems", source = "shoppingCart.cartItems")
    Order shoppingCartToOrder(ShoppingCart shoppingCart, String shippingAddress);

    @AfterMapping
    default void updateOrder(@MappingTarget Order order) {
        order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));
    }

    @Named("total")
    default BigDecimal getTotal(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(i -> i.getBook().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
