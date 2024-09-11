package mate.academy.bookstore.repository.cartitem;

import mate.academy.bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci "
            + "LEFT JOIN FETCH ci.shoppingCart sc "
            + "LEFT JOIN FETCH ci.book b "
            + "WHERE ci.id = :id AND sc.id = :shoppingCartId")
    CartItem getByIdAndShoppingCartId(@Param("id") Long id,
                                      @Param("shoppingCartId") Long shoppingCartId);
}
