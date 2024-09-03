package mate.academy.bookstore.repository.cartitem;

import java.util.Set;
import mate.academy.bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Set<CartItem> getCartItemsByShoppingCartId(Long shoppingCartId);

    @Query("SELECT ci FROM CartItem ci "
            + "LEFT JOIN FETCH ci.shoppingCart sc "
            + "LEFT JOIN FETCH ci.book b "
            + "WHERE ci.id = :id AND sc.id = :shoppingCartId")
    CartItem getByIdAndShoppingCartId(Long id, Long shoppingCartId);
}
