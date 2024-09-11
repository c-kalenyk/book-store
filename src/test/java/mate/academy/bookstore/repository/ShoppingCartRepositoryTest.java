package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find shopping cart by user id")
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql",
            "classpath:database/users/add-user.sql",
            "classpath:database/shopping_carts/add-shopping-cart-for-bob.sql",
            "classpath:database/cart_items/add-items-to-bobs-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items.sql",
            "classpath:database/shopping_carts/delete-shopping-carts.sql",
            "classpath:database/users/delete-users.sql",
            "classpath:database/books/delete-books.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void findByUserId_GivenValidUserId_ShouldReturnShoppingCart() {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(1L);

        assertEquals(3, shoppingCart.getCartItems().size());
    }
}
