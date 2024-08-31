package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.service.CartItemService;
import mate.academy.bookstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing cart items")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class ShoppingCartController {
    private final CartItemService cartItemService;
    private final UserService userService;

    @Operation(summary = "Get all books", description = "Get a list of all available books")
    @GetMapping
    public ShoppingCartDto getAllCartItems(Authentication authentication) {
        UserResponseDto user = userService.findByEmail(authentication.getName());
        return cartItemService.getCartItems(user.getId());
    }

    @Operation(summary = "Add a new cart item", description = "Add a new cart item "
            + "to shopping cart")
    @PostMapping
    public CartItemDto saveCartItem(Authentication authentication,
                                    @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        UserResponseDto user = userService.findByEmail(authentication.getName());
        return cartItemService.save(user.getId(), requestDto);
    }

    @Operation(summary = "Update cart item quantity", description = "Update "
            + "cart item quantity by id")
    @PutMapping("/items/{cartItemId}")
    public CartItemDto updateCartItemQuantity(@PathVariable Long cartItemId,
                              @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        return cartItemService.updateQuantity(cartItemId, requestDto);
    }

    @Operation(summary = "Delete cart item", description = "Delete cart item by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteById(cartItemId);
    }
}
