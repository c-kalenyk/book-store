package mate.academy.bookstore.repository.shoppingcart;

import mate.academy.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT sc FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.user u "
            + "LEFT JOIN FETCH sc.cartItems ci "
            + "WHERE u.id = :userId")
    ShoppingCart findByUserId(@Param("userId") Long userId);
}
