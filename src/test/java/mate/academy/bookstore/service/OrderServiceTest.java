package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.OrderUtil.createTestCreateOrderRequestDto;
import static mate.academy.bookstore.util.OrderUtil.createTestOrder;
import static mate.academy.bookstore.util.OrderUtil.createTestOrderDto;
import static mate.academy.bookstore.util.OrderUtil.createTestUpdateOrderRequestDto;
import static mate.academy.bookstore.util.ShoppingCartTestUtil.createTestShoppingCart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.exception.OrderProcessingException;
import mate.academy.bookstore.mapper.OrderItemMapper;
import mate.academy.bookstore.mapper.OrderMapper;
import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.order.OrderRepository;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Verify that correct OrderDto was returned when calling save() method")
    public void create_WithValidRequestDto_ShouldReturnValidOrderDto() {
        //Given
        ShoppingCart shoppingCart = createTestShoppingCart();
        Order order = createTestOrder();
        CreateOrderRequestDto requestDto = createTestCreateOrderRequestDto(order);
        OrderDto expected = createTestOrderDto(order);

        when(shoppingCartRepository.findByUserId(order.getUser().getId())).thenReturn(shoppingCart);
        when(orderMapper.shoppingCartToOrder(shoppingCart, requestDto.getShippingAddress()))
                .thenReturn(order);
        doNothing().when(cartItemRepository).deleteAll(shoppingCart.getCartItems());
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);
        //When
        OrderDto actual = orderService.create(order.getUser().getId(), requestDto);
        //Then
        assertEquals(expected, actual);

        verify(shoppingCartRepository, times(1)).findByUserId(
                shoppingCart.getUser().getId()
        );
        verify(orderMapper, times(1)).shoppingCartToOrder(
                shoppingCart, requestDto.getShippingAddress()
        );
        verify(cartItemRepository, times(1)).deleteAll(
                shoppingCart.getCartItems()
        );
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).toDto(order);
        verifyNoMoreInteractions(shoppingCartRepository, orderMapper,
                cartItemRepository, orderRepository, orderMapper
        );
    }

    @Test
    @DisplayName("""
    Verify that exception is thrown when calling create() method with empty shopping cart""")
    public void create_WithEmptyShoppingCart_ShouldThrowException() {
        //Given
        ShoppingCart shoppingCart = createTestShoppingCart();
        shoppingCart.getCartItems().clear();
        CreateOrderRequestDto requestDto = createTestCreateOrderRequestDto(createTestOrder());
        Long userId = shoppingCart.getUser().getId();

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(shoppingCart);
        //When
        Exception exception = assertThrows(
                OrderProcessingException.class,
                () -> orderService.create(userId, requestDto)
        );
        //Then
        String expected = "Cart is empty for user by id: " + userId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(shoppingCartRepository, times(1)).findByUserId(userId);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
    Verify that correct OrderDto was returned when calling updateOrderStatus() method""")
    public void updateOrderStatus_WithValidRequestDto_ShouldReturnValidOrderDto() {
        //Given
        Order order = createTestOrder();
        UpdateOrderRequestDto requestDto = createTestUpdateOrderRequestDto(order);
        OrderDto expected = createTestOrderDto(order);
        expected.setStatus(String.valueOf(requestDto.getStatus()));
        Long id = order.getId();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);
        //When
        OrderDto actual = orderService.updateOrderStatus(id, requestDto);
        //Then
        assertEquals(expected, actual);

        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, times(1))
                .save(order);
        verify(orderMapper, times(1)).toDto(order);
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("""
    Verify that exception is thrown when calling updateOrderStatus() method with invalid id""")
    public void updateOrderStatus_WithInvalidId_ShouldThrowException() {
        //Given
        Long id = 2L;
        UpdateOrderRequestDto requestDto = createTestUpdateOrderRequestDto(createTestOrder());

        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.updateOrderStatus(id, requestDto)
        );
        //Then
        String expected = "Can't find existing order by id: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(orderRepository, times(1)).findById(id);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("""
    Verify that correct order history was returned when calling getUserOrders() method""")
    public void getUserOrders_WithValidUserId_ShouldReturnValidOrderDto() {
        //Given
        Order order = createTestOrder();
        OrderDto orderDto = createTestOrderDto(order);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Order> ordersPage = new PageImpl<>(List.of(order), pageable, 1);
        Set<OrderDto> expected = Set.of(orderDto);
        Long userId = order.getUser().getId();

        when(orderRepository.findByUserId(userId, pageable)).thenReturn(ordersPage);
        when(orderMapper.toDtoSet(ordersPage.getContent())).thenReturn(expected);
        //When
        Set<OrderDto> actual = orderService.getUserOrders(userId, pageable);
        //Then
        assertEquals(expected, actual);

        verify(orderRepository, times(1)).findByUserId(userId, pageable);
        verify(orderMapper, times(1)).toDtoSet(ordersPage.getContent());
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("""
    Verify that correct OrderItemDto was returned when calling getSpecificItem() method""")
    public void getSpecificItem_WithValidIds_ShouldReturnValidOrderItemDto() {
        //Given
        Order order = createTestOrder();
        OrderDto orderDto = createTestOrderDto(order);
        OrderItem orderItem = order.getOrderItems().iterator().next();
        OrderItemDto expected = orderDto.getOrderItems().get(0);
        Long userId = order.getUser().getId();
        Long orderId = order.getId();
        Long itemId = expected.getId();

        when(orderItemRepository.findSpecificItem(userId, orderId, itemId))
                .thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toDto(orderItem)).thenReturn(expected);
        //When
        OrderItemDto actual = orderService.getSpecificItem(userId, orderId, itemId);
        //Then
        assertEquals(expected, actual);

        verify(orderItemRepository, times(1))
                .findSpecificItem(userId, orderId, itemId);
        verify(orderItemMapper, times(1)).toDto(orderItem);
        verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }

    @Test
    @DisplayName("""
    Verify that exception is thrown when calling getSpecificItem() method with invalid item id""")
    public void getSpecificItem_WithInvalidItemId_ShouldThrowException() {
        //Given
        Long id = 1L;
        Long itemId = 2L;

        when(orderItemRepository.findSpecificItem(id, id, itemId)).thenReturn(Optional.empty());
        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.getSpecificItem(id, id, itemId)
        );
        //Then
        String expected = "Can't get order item by order id " + id + " and item id " + itemId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(orderItemRepository, times(1)).findSpecificItem(id, id, itemId);
        verifyNoMoreInteractions(orderItemRepository);
    }

    @Test
    @DisplayName("""
    Verify that correct items from order was returned when calling getItemsFromOrder() method""")
    public void getItemsFromOrder_WithValidIds_ShouldReturnValidOrderItemDto() {
        //Given
        Order order = createTestOrder();
        OrderDto orderDto = createTestOrderDto(order);
        Set<OrderItem> orderItems = order.getOrderItems();
        Set<OrderItemDto> expected = new HashSet<>(orderDto.getOrderItems());
        Long userId = order.getUser().getId();
        Long orderId = order.getId();

        when(orderItemRepository.findByOrder(userId, orderId)).thenReturn(orderItems);
        when(orderItemMapper.toDtoSet(orderItems)).thenReturn(expected);
        //When
        Set<OrderItemDto> actual = orderService.getItemsFromOrder(userId, orderId);
        //Then
        assertEquals(expected, actual);

        verify(orderItemRepository, times(1)).findByOrder(userId, orderId);
        verify(orderItemMapper, times(1)).toDtoSet(orderItems);
        verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }
}
