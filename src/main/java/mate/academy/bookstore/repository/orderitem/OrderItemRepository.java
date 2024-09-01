package mate.academy.bookstore.repository.orderitem;

import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Set<OrderItem> findByOrderId(Long orderId);

    Optional<OrderItem> findByOrderIdAndId(Long orderId, Long itemId);
}
