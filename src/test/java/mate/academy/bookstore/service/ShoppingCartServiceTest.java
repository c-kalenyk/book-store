package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.CartItemTestUtil.createTestCreateCartItemRequestDto;
import static mate.academy.bookstore.util.CartItemTestUtil.createTestUpdateCartItemRequestDto;
import static mate.academy.bookstore.util.ShoppingCartTestUtil.createTestShoppingCart;
import static mate.academy.bookstore.util.ShoppingCartTestUtil.createTestShoppingCartDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CartItemMapper;
import mate.academy.bookstore.mapper.ShoppingCartMapper;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.repository.cartitem.CartItemRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Verify that correct ShoppingCartDto was returned when calling save() method")
    public void save_WithValidRequestDto_ShouldReturnValidShoppingCartDto() {
        //Given
        ShoppingCart shoppingCart = createTestShoppingCart();
        CartItem cartItem = shoppingCart.getCartItems().iterator().next();
        CreateCartItemRequestDto requestDto = createTestCreateCartItemRequestDto(cartItem);
        ShoppingCartDto expected = createTestShoppingCartDto(shoppingCart);
        Long shoppingCartId = shoppingCart.getId();

        when(bookRepository.findById(requestDto.getBookId())).thenReturn(
                Optional.of(cartItem.getBook())
        );
        when(shoppingCartRepository.findById(shoppingCartId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemMapper.toModel(requestDto)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        //When
        ShoppingCartDto actual = shoppingCartService.save(shoppingCartId, requestDto);
        //Then
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(requestDto.getBookId());
        verify(shoppingCartRepository, times(1)).findById(shoppingCartId);
        verify(cartItemMapper, times(1)).toModel(requestDto);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(bookRepository, shoppingCartRepository,
                cartItemMapper, cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
    Verify that correct ShoppingCartDto was returned when calling updateQuantity() method""")
    public void updateQuantity_WithValidRequestDto_ShouldReturnValidShoppingCartDto() {
        //Given
        ShoppingCart shoppingCart = createTestShoppingCart();
        CartItem cartItem = shoppingCart.getCartItems().iterator().next();
        UpdateCartItemRequestDto requestDto = createTestUpdateCartItemRequestDto();
        cartItem.setQuantity(requestDto.getQuantity());
        ShoppingCartDto expected = createTestShoppingCartDto(shoppingCart);
        expected.getCartItems().iterator().next().setQuantity(requestDto.getQuantity());
        Long shoppingCartId = shoppingCart.getId();
        Long cartItemId = cartItem.getId();

        when(shoppingCartRepository.findById(shoppingCartId))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.getByIdAndShoppingCartId(cartItemId, shoppingCartId))
                .thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        //When
        ShoppingCartDto actual = shoppingCartService.updateQuantity(
                shoppingCartId, cartItemId, requestDto);
        //Then
        assertEquals(expected, actual);

        verify(shoppingCartRepository, times(1)).findById(shoppingCartId);
        verify(cartItemRepository, times(1))
                .getByIdAndShoppingCartId(cartItemId, shoppingCartId);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
    Verify that correct ShoppingCartDto was returned when calling getShoppingCart() method""")
    public void getShoppingCart_WithValidId_ShouldReturnValidShoppingCartDto() {
        //Given
        ShoppingCart shoppingCart = createTestShoppingCart();
        ShoppingCartDto expected = createTestShoppingCartDto(shoppingCart);
        Long id = shoppingCart.getId();

        when(shoppingCartRepository.findById(id)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        //When
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(id);
        //Then
        assertEquals(expected, actual);

        verify(shoppingCartRepository, times(1)).findById(id);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
    Verify that exception is thrown when calling getShoppingCart() method with invalid id""")
    public void getShoppingCart_WithInvalidId_ShouldThrowException() {
        //Given
        Long id = 2L;

        when(shoppingCartRepository.findById(id)).thenReturn(Optional.empty());
        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCart(id)
        );
        //Then
        String expected = "Can't find shopping cart by id: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(shoppingCartRepository, times(1)).findById(id);
        verifyNoMoreInteractions(shoppingCartRepository);
    }
}
