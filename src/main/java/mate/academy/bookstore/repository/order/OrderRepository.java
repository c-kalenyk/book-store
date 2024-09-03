package mate.academy.bookstore.repository.order;

import java.util.Optional;
import mate.academy.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user u "
            + "LEFT JOIN FETCH o.orderItems "
            + "WHERE u.id = :userId")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user u "
            + "LEFT JOIN FETCH o.orderItems "
            + "WHERE o.id = :id")
    Optional<Order> findById(Long id);
}
