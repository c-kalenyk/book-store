package mate.academy.bookstore.util;

import static mate.academy.bookstore.util.BookTestUtil.createTestBook;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;

public class CartItemTestUtil {
    public static CartItem createTestCartItem(ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(createTestBook());
        cartItem.setQuantity(1);
        return cartItem;
    }

    public static CreateCartItemRequestDto createTestCreateCartItemRequestDto(CartItem cartItem) {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(cartItem.getBook().getId());
        requestDto.setQuantity(cartItem.getQuantity());
        return requestDto;
    }

    public static UpdateCartItemRequestDto createTestUpdateCartItemRequestDto() {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(2);
        return requestDto;
    }

    public static CartItemDto createTestCartItemDto(CartItem cartItem) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setBookId(cartItem.getBook().getId());
        cartItemDto.setBookTitle(cartItem.getBook().getTitle());
        cartItemDto.setQuantity(cartItem.getQuantity());
        return cartItemDto;
    }

    public static Set<CartItemDto> fillExpectedCartItemDtoSet() {
        Set<CartItemDto> expected = new HashSet<>();
        expected.add(new CartItemDto().setId(1L).setBookId(1L).setBookTitle("Title 1")
                .setQuantity(1));
        expected.add(new CartItemDto().setId(2L).setBookId(2L).setBookTitle("Title 2")
                .setQuantity(2));
        expected.add(new CartItemDto().setId(3L).setBookId(3L).setBookTitle("Title 3")
                .setQuantity(3));
        return expected;
    }

    public static Set<CartItemDto> sortCartItems(Set<CartItemDto> cartItems) {
        return cartItems.stream()
                .sorted(Comparator.comparing(CartItemDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
