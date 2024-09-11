package mate.academy.bookstore.util;

import static mate.academy.bookstore.util.CartItemTestUtil.createTestCartItem;
import static mate.academy.bookstore.util.ShoppingCartTestUtil.createTestShoppingCart;
import static mate.academy.bookstore.util.UserTestUtil.createTestUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;

public class OrderUtil {
    public static Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(createTestUser());
        ShoppingCart shoppingCart = createTestShoppingCart();
        Set<CartItem> cartItems = Set.of(createTestCartItem(shoppingCart));
        order.setTotal(countTotalPrice(cartItems));
        order.setOrderDate(LocalDateTime.of(2011, 11, 11, 11, 11, 11));
        order.setShippingAddress(order.getUser().getShippingAddress());
        order.setOrderItems(new HashSet<>(Set.of(createTestOrderItem(order,
                cartItems.iterator().next()))));
        return order;
    }

    public static CreateOrderRequestDto createTestCreateOrderRequestDto(Order order) {
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setShippingAddress(order.getUser().getShippingAddress());
        return requestDto;
    }

    public static UpdateOrderRequestDto createTestUpdateOrderRequestDto(Order order) {
        UpdateOrderRequestDto requestDto = new UpdateOrderRequestDto();
        requestDto.setStatus(Order.Status.DELIVERED);
        return requestDto;
    }

    public static OrderDto createTestOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setOrderItems(order.getOrderItems().stream()
                .map(OrderUtil::createTestOrderItemDto)
                .toList());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setTotal(order.getTotal());
        orderDto.setStatus(String.valueOf(order.getStatus()));
        return orderDto;
    }

    public static OrderItem createTestOrderItem(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        return orderItem;
    }

    public static OrderItemDto createTestOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setBookId(orderItem.getBook().getId());
        orderItemDto.setQuantity(orderItem.getQuantity());
        return orderItemDto;
    }

    private static BigDecimal countTotalPrice(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(i -> i.getBook().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
