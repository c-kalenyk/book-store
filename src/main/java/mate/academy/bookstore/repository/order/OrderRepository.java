package mate.academy.bookstore.repository.order;

import java.util.Optional;
import java.util.Set;
import mate.academy.bookstore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user u "
            + "LEFT JOIN FETCH o.orderItems "
            + "WHERE u.id = :userId")
    Set<Order> findByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user u "
            + "LEFT JOIN FETCH o.orderItems "
            + "WHERE o.id = :id")
    Optional<Order> findById(Long id);
}
