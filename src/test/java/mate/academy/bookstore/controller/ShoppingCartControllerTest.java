package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.CartItemTestUtil.createTestCreateCartItemRequestDto;
import static mate.academy.bookstore.util.CartItemTestUtil.createTestUpdateCartItemRequestDto;
import static mate.academy.bookstore.util.CartItemTestUtil.fillExpectedCartItemDtoSet;
import static mate.academy.bookstore.util.CartItemTestUtil.sortCartItems;
import static mate.academy.bookstore.util.ShoppingCartTestUtil.createTestShoppingCart;
import static mate.academy.bookstore.util.ShoppingCartTestUtil.createTestShoppingCartDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.model.ShoppingCart;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-user.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_carts/add-shopping-cart-for-bob.sql")
            );
        }
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Get shopping cart with all cart items")
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql",
            "classpath:database/cart_items/add-items-to-bobs-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items.sql",
            "classpath:database/books/delete-books.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getShoppingCart_GivenShoppingCart_ShouldReturnShoppingCart() throws Exception {
        //Given
        ShoppingCartDto expected = createTestShoppingCartDto(createTestShoppingCart());
        Set<CartItemDto> cartItems = fillExpectedCartItemDtoSet();
        expected.getCartItems().clear();
        expected.getCartItems().addAll(cartItems);
        //When
        MvcResult result = mockMvc.perform(
                get("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );
        assertEquals(3, actual.getCartItems().size());
        assertEquals(expected, actual);
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Save cart item to shopping cart")
    @Sql(scripts = {
            "classpath:database/books/add-one-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items.sql",
            "classpath:database/books/delete-books.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void saveCartItem_ValidRequestDto_ShouldSaveCartItem() throws Exception {
        //Given
        ShoppingCart shoppingCart = createTestShoppingCart();
        CreateCartItemRequestDto requestDto = createTestCreateCartItemRequestDto(
                shoppingCart.getCartItems().iterator().next());
        ShoppingCartDto expected = createTestShoppingCartDto(shoppingCart);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                post("/cart")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        //Then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Update cart item quantity")
    @Sql(scripts = {
            "classpath:database/books/add-one-book.sql",
            "classpath:database/cart_items/add-one-item-to-bobs-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items.sql",
            "classpath:database/books/delete-books.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateCartItemQuantity_ValidRequestDto_ShouldUpdateQuantity() throws Exception {
        //Given
        ShoppingCart shoppingCart = createTestShoppingCart();
        UpdateCartItemRequestDto requestDto = createTestUpdateCartItemRequestDto();
        ShoppingCartDto expected = createTestShoppingCartDto(shoppingCart);
        expected.getCartItems().iterator().next().setQuantity(requestDto.getQuantity());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                put("/cart/items/1")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Delete cart item by id")
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql",
            "classpath:database/cart_items/add-items-to-bobs-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items.sql",
            "classpath:database/books/delete-books.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void deleteCartItem_GivenShoppingCart_ShouldDeleteCartItem() throws Exception {
        //Given
        ShoppingCartDto expected = createTestShoppingCartDto(createTestShoppingCart());
        Set<CartItemDto> cartItems = fillExpectedCartItemDtoSet();
        expected.getCartItems().clear();
        expected.getCartItems().addAll(cartItems);
        expected.getCartItems().removeIf(cartItem -> cartItem.getId() == 1);
        //When
        mockMvc.perform(
                delete("/cart/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        MvcResult result = mockMvc.perform(
                get("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );

        expected.setCartItems(sortCartItems(expected.getCartItems()));
        actual.setCartItems(sortCartItems(actual.getCartItems()));

        assertEquals(2, actual.getCartItems().size());
        assertEquals(expected, actual);
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_carts/delete-shopping-carts.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/delete-users.sql")
            );
        }
    }
}
