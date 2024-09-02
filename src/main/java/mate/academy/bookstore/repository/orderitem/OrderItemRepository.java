package mate.academy.bookstore.repository.orderitem;

import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi "
            + "LEFT JOIN FETCH oi.order o "
            + "LEFT JOIN FETCH oi.book b "
            + "LEFT JOIN FETCH o.user u "
            + "WHERE u.id = :userId AND o.id = :orderId")
    Set<OrderItem> findByOrder(@Param("userId") Long userId, @Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi "
            + "LEFT JOIN FETCH oi.order o "
            + "LEFT JOIN FETCH oi.book b "
            + "LEFT JOIN FETCH o.user u "
            + "WHERE u.id = :userId AND o.id = :orderId AND oi.id = :itemId")
    Optional<OrderItem> findSpecificItem(@Param("userId") Long userId,
                                         @Param("orderId") Long orderId,
                                         @Param("itemId") Long itemId);
}
