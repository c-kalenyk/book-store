package mate.academy.bookstore.util;

import static mate.academy.bookstore.util.CartItemTestUtil.createTestCartItem;
import static mate.academy.bookstore.util.UserTestUtil.createTestUser;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.model.ShoppingCart;

public class ShoppingCartTestUtil {
    public static ShoppingCart createTestShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(createTestUser());
        shoppingCart.setId(shoppingCart.getUser().getId());
        shoppingCart.setCartItems(new HashSet<>(Set.of(createTestCartItem(shoppingCart))));
        return shoppingCart;
    }

    public static ShoppingCartDto createTestShoppingCartDto(ShoppingCart shoppingCart) {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(shoppingCart.getId());
        shoppingCartDto.setUserId(shoppingCart.getUser().getId());
        shoppingCartDto.setCartItems(shoppingCart.getCartItems().stream()
                .map(CartItemTestUtil::createTestCartItemDto)
                .collect(Collectors.toSet()));
        return shoppingCartDto;
    }
}
