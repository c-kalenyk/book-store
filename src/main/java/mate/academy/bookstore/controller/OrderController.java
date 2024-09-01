package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.service.OrderItemService;
import mate.academy.bookstore.service.OrderService;
import mate.academy.bookstore.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UserService userService;

    @Operation(summary = "Get all orders from user",
            description = "Get history of orders made by user")
    @GetMapping
    public Set<OrderDto> getOrdersHistory(Authentication authentication) {
        UserResponseDto user = userService.findByEmail(authentication.getName());
        return orderService.getUserOrders(user.getId());
    }

    @Operation(summary = "Create new order", description = "Create new order from shopping cart")
    @PostMapping
    public OrderDto createOrder(Authentication authentication,
                                @RequestBody @Valid CreateOrderRequestDto requestDto) {
        UserResponseDto user = userService.findByEmail(authentication.getName());
        return orderService.create(user.getId(), requestDto);
    }

    @Operation(summary = "Update order status", description = "Update order status")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid UpdateOrderRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }

    @Operation(summary = "Get order item", description = "Get order item by id")
    @GetMapping("{orderId}/items/{itemId}")
    OrderItemDto getOrderItem(@PathVariable Long orderId,
                              @PathVariable Long itemId) {
        return orderItemService.getByOrderIdAndItemId(orderId, itemId);
    }

    @Operation(summary = "Get all items for a specific order")
    @GetMapping("/{orderId}/items")
    Set<OrderItemDto> getAllOrderItems(@PathVariable Long orderId) {
        return orderItemService.getItemsFromOrder(orderId);
    }
}
