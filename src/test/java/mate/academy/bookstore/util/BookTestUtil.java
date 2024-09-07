package mate.academy.bookstore.util;

import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.model.Category;

public class BookTestUtil {
    public static Book createTestBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("Isbn");
        book.setPrice(BigDecimal.valueOf(1.99));
        book.setDescription("Description");
        book.setCoverImage("Cover image");
        Category category = createTestCategory();
        book.setCategories(Set.of(category));
        return book;
    }

    public static CreateBookRequestDto createTestBookRequestDto(Book book) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(book.getTitle());
        requestDto.setAuthor(book.getAuthor());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setPrice(book.getPrice());
        requestDto.setDescription(book.getDescription());
        requestDto.setCoverImage(book.getCoverImage());
        requestDto.setCategories(book.getCategories());
        return requestDto;
    }

    public static BookDto createTestBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        if (book.getCategories() != null) {
            bookDto.setCategoryIds(book.getCategories()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));
        } else {
            bookDto.setCategoryIds(Collections.emptySet());
        }
        return bookDto;
    }

    public static Book createTestBookWithOnlyMandatoryFields() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setIsbn("Isbn");
        book.setPrice(BigDecimal.valueOf(1.99));
        return book;
    }

    public static List<BookDto> fillExpectedBookDtoList() {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L).setTitle("Title 1").setAuthor("Author 1")
                .setIsbn("Isbn 1").setPrice(BigDecimal.valueOf(1.99))
                .setDescription("Description 1").setCoverImage("Cover image 1"));
        expected.add(new BookDto().setId(2L).setTitle("Title 2").setAuthor("Author 2")
                .setIsbn("Isbn 2").setPrice(BigDecimal.valueOf(1.99))
                .setDescription("Description 2").setCoverImage("Cover image 2"));
        expected.add(new BookDto().setId(3L).setTitle("Title 3").setAuthor("Author 3")
                .setIsbn("Isbn 3").setPrice(BigDecimal.valueOf(1.99))
                .setDescription("Description 3").setCoverImage("Cover image 3"));
        return expected;
    }
}
