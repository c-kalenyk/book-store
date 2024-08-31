package mate.academy.bookstore.repository.cartitem;

import java.util.Set;
import mate.academy.bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Set<CartItem> getCartItemsByShoppingCartId(Long shoppingCartId);
}
