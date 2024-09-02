package mate.academy.bookstore.service;

import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto save(Long shoppingCartId, CreateCartItemRequestDto requestDto);

    ShoppingCartDto updateQuantity(Long userId, Long id, UpdateCartItemRequestDto requestDto);

    ShoppingCartDto getShoppingCart(Long shoppingCartId);

    void deleteById(Long id);
}
