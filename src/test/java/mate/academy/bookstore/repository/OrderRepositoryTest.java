package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import mate.academy.bookstore.model.Order;
import mate.academy.bookstore.repository.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Find orders by user id")
    @Sql(scripts = {
            "classpath:database/users/add-user.sql",
            "classpath:database/orders/add-order.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/orders/delete-orders.sql",
            "classpath:database/users/delete-users.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void findByUserId_GivenValidUserId_ShouldReturnOrder() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Order> page = orderRepository.findByUserId(1L, pageable);
        Order actual = page.get().iterator().next();

        assertEquals(1L, actual.getId());
    }
}
