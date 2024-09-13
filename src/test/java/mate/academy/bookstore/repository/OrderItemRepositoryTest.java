package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.model.OrderItem;
import mate.academy.bookstore.repository.orderitem.OrderItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("Find orders items by order id")
    @Sql(scripts = {
            "classpath:database/users/add-user.sql",
            "classpath:database/orders/add-order.sql",
            "classpath:database/books/add-one-book.sql",
            "classpath:database/order_items/add-order-item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/order_items/delete-order-items.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/orders/delete-orders.sql",
            "classpath:database/users/delete-users.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void findByOrder_GivenValidOrderId_ShouldReturnOrderItems() {
        Set<OrderItem> actual = orderItemRepository.findByOrder(1L, 1L);

        assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("Find orders by user id")
    @Sql(scripts = {
            "classpath:database/users/add-user.sql",
            "classpath:database/orders/add-order.sql",
            "classpath:database/books/add-one-book.sql",
            "classpath:database/order_items/add-order-item.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/order_items/delete-order-items.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/orders/delete-orders.sql",
            "classpath:database/users/delete-users.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void findSpecificItem_GivenValidIds_ShouldReturnOrderItem() {
        Optional<OrderItem> actual = orderItemRepository.findSpecificItem(1L, 1L, 1L);

        assertEquals(1L, actual.get().getId());
    }
}
