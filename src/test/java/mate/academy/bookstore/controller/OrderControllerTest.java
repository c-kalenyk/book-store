package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.OrderUtil.createTestCreateOrderRequestDto;
import static mate.academy.bookstore.util.OrderUtil.createTestOrder;
import static mate.academy.bookstore.util.OrderUtil.createTestOrderDto;
import static mate.academy.bookstore.util.OrderUtil.createTestUpdateOrderRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.bookstore.dto.order.CreateOrderRequestDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.UpdateOrderRequestDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.model.Order;
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
public class OrderControllerTest {
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
                    new ClassPathResource("database/roles/add-roles.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-user.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users_roles/set-admin-role-for-bob.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shopping_carts/add-shopping-cart-for-bob.sql")
            );
        }
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Get all orders made by user")
    @Sql(scripts = {
            "classpath:database/orders/add-order.sql",
            "classpath:database/books/add-one-book.sql",
            "classpath:database/order_items/add-order-item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/order_items/delete-order-items.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/orders/delete-orders.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getOrdersHistory_GivenOrder_ShouldReturnOrders() throws Exception {
        //Given
        Set<OrderDto> expected = Set.of(createTestOrderDto(createTestOrder()));
        //When
        MvcResult result = mockMvc.perform(
                get("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        OrderDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderDto[].class
        );
        assertEquals(1, actual.length);
        assertEquals(expected, Arrays.stream(actual).collect(Collectors.toSet()));
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Create order from shopping cart")
    @Sql(scripts = {
            "classpath:database/books/add-one-book.sql",
            "classpath:database/cart_items/add-one-item-to-bobs-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/order_items/delete-order-items.sql",
            "classpath:database/cart_items/delete-cart-items.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/orders/delete-orders.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void createOrder_ValidRequestDto_ShouldSaveOrder() throws Exception {
        //Given
        Order order = createTestOrder();
        CreateOrderRequestDto requestDto = createTestCreateOrderRequestDto(order);
        OrderDto expected = createTestOrderDto(order);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                post("/orders")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        //Then
        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Update order status")
    @Sql(scripts = {
            "classpath:database/orders/add-order.sql",
            "classpath:database/books/add-one-book.sql",
            "classpath:database/order_items/add-order-item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/order_items/delete-order-items.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/orders/delete-orders.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateOrderStatus_ValidRequestDto_ShouldUpdateStatus() throws Exception {
        //Given
        Order order = createTestOrder();
        UpdateOrderRequestDto requestDto = createTestUpdateOrderRequestDto(order);
        OrderDto expected = createTestOrderDto(order);
        expected.setStatus(String.valueOf(Order.Status.DELIVERED));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                patch("/orders/1")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Get specific order item from order")
    @Sql(scripts = {
            "classpath:database/orders/add-order.sql",
            "classpath:database/books/add-one-book.sql",
            "classpath:database/order_items/add-order-item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/order_items/delete-order-items.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/orders/delete-orders.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getOrderItem_GivenOrderItem_ShouldReturnOrderItem() throws Exception {
        //Given
        OrderDto orderDto = createTestOrderDto(createTestOrder());
        OrderItemDto expected = orderDto.getOrderItems().get(0);
        //When
        MvcResult result = mockMvc.perform(
                get("/orders/1/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        OrderItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderItemDto.class
        );
        assertEquals(expected, actual);
    }

    @Test
    @WithUserDetails("bob@test.com")
    @DisplayName("Get all order items from order")
    @Sql(scripts = {
            "classpath:database/orders/add-order.sql",
            "classpath:database/books/add-one-book.sql",
            "classpath:database/order_items/add-order-item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/order_items/delete-order-items.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/orders/delete-orders.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getAllOrderItems_GivenOrder_ShouldReturnOrderItems() throws Exception {
        //Given
        OrderDto orderDto = createTestOrderDto(createTestOrder());
        List<OrderItemDto> expected = orderDto.getOrderItems();
        //When
        MvcResult result = mockMvc.perform(
                get("/orders/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        OrderItemDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), OrderItemDto[].class
        );
        assertEquals(1, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
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
                    new ClassPathResource("database/users_roles/remove-roles-from-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/delete-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/roles/delete-roles.sql")
            );
        }
    }
}
