package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.BookTestUtil.createTestBook;
import static mate.academy.bookstore.util.BookTestUtil.createTestBookDto;
import static mate.academy.bookstore.util.BookTestUtil.createTestBookRequestDto;
import static mate.academy.bookstore.util.BookTestUtil.fillExpectedBookDtoList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.model.Book;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Get a list of all available books")
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/books/delete-books.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getAllBooks_GivenBooksInCatalog_ShouldReturnAllBooks() throws Exception {
        //Given
        List<BookDto> expected = fillExpectedBookDtoList();
        //When
        MvcResult result = mockMvc.perform(
                get("/books")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Get book by id")
    @Sql(scripts = {
            "classpath:database/books/add-one-book.sql",
            "classpath:database/categories/add-category.sql",
            "classpath:database/books_categories/add-category-to-the-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/books_categories/remove-categories-from-the-books.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/categories/delete-categories.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getBookById_GivenBook_ShouldReturnBook() throws Exception {
        //Given
        Book book = createTestBook();
        BookDto expected = createTestBookDto(book);
        //When
        MvcResult result = mockMvc.perform(
                get("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new book")
    @Sql(scripts = {
            "classpath:database/categories/add-category.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/books_categories/remove-categories-from-the-books.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/categories/delete-categories.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void createBook_ValidRequestDto_ShouldCreateBook() throws Exception {
        //Given
        Book book = createTestBook();
        CreateBookRequestDto requestDto = createTestBookRequestDto(book);
        BookDto expected = createTestBookDto(book);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update book by id")
    @Sql(scripts = {
            "classpath:database/books/add-one-book.sql",
            "classpath:database/categories/add-category.sql",
            "classpath:database/books_categories/add-category-to-the-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/books_categories/remove-categories-from-the-books.sql",
            "classpath:database/books/delete-books.sql",
            "classpath:database/categories/delete-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateBook_ValidRequestDto_ShouldUpdateBook() throws Exception {
        //Given
        Book book = createTestBook();
        CreateBookRequestDto requestDto = createTestBookRequestDto(book);
        BookDto expected = createTestBookDto(book);
        requestDto.setTitle("Updated title");
        expected.setTitle("Updated title");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                put("/books/1")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete book by id")
    @Sql(scripts = {
            "classpath:database/books/add-three-books.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete-books.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void deleteBook_GivenBooks_ShouldDeleteBook() throws Exception {
        //Given
        List<BookDto> expected = fillExpectedBookDtoList();
        expected.remove(1);
        //When
        mockMvc.perform(
                delete("/books/2")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        MvcResult result = mockMvc.perform(
                get("/books")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }
}
