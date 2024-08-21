package mate.academy.bookstore;

import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book testBook = new Book();
                testBook.setTitle("Lord of the Rings");
                testBook.setAuthor("J. R. R. Tolkien");
                testBook.setIsbn("12345");
                testBook.setPrice(BigDecimal.valueOf(321));

                bookService.save(testBook);
                System.out.println(bookService.findAll());
            }
        };
    }
}
