package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;

public interface CartItemService {
    CartItemDto save(Long shoppingCartId, CreateCartItemRequestDto requestDto);

    CartItemDto updateQuantity(Long id, UpdateCartItemRequestDto requestDto);

    ShoppingCartDto getCartItems(Long shoppingCartId);

    void deleteById(Long id);
}