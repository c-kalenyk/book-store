package mate.academy.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.book.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by category")
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql",
            "classpath:database/categories/add-category.sql",
            "classpath:database/books_categories/add-categories-for-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/books_categories/remove-categories-from-the-books.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/categories/delete-categories.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void findAllByCategoryId_TwoBooksWithSameCategory_ShouldReturnTwoBooks() {
        List<Book> books = bookRepository.findAllByCategoryId(1L);

        assertEquals(2, books.size());
    }
}
