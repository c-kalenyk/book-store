package mate.academy.bookstore.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.exception.OrderProcessingException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Transactional
    @Override
    public OrderDto create(Long userId, CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new OrderProcessingException("Cart is empty for user by id: " + userId);
        }
        Order order = orderMapper.shoppingCartToOrder(shoppingCart,
                requestDto.getShippingAddress());
        cartItemRepository.deleteAll(cartItems);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Set<OrderDto> getUserOrders(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);
        return orderMapper.toDtoSet(ordersPage.getContent());
    }

    @Override
    public OrderDto updateOrderStatus(Long id, UpdateOrderRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find existing order by id: " + id));
        order.setStatus(requestDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderItemDto getSpecificItem(Long userId, Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findSpecificItem(userId, orderId, itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get order item by order id " + orderId + " and item id " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public Set<OrderItemDto> getItemsFromOrder(Long userId, Long orderId) {
        Set<OrderItem> orderItems = orderItemRepository.findByOrder(userId, orderId);
        return orderItemMapper.toDtoSet(orderItems);
    }
}
