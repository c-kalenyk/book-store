package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.BookTestUtil.fillExpectedBookDtoList;
import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategory;
import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategoryDto;
import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategoryRequestDto;
import static mate.academy.bookstore.util.CategoryTestUtil.fillExpectedCategoryDtoList;
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
import java.util.Set;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.model.Category;
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
public class CategoryControllerTest {
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
    @DisplayName("Get a list of all available categories")
    @Sql(scripts = {
            "classpath:database/categories/add-two-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/categories/delete-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getAllCategories_GivenCategoriesInCatalog_ShouldReturnAllCategories()
            throws Exception {
        //Given
        List<CategoryDto> expected = fillExpectedCategoryDtoList();
        //When
        MvcResult result = mockMvc.perform(
                get("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class
        );
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Get category by id")
    @Sql(scripts = {
            "classpath:database/categories/add-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/categories/delete-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void getCategoryById_GivenBook_ShouldReturnBook() throws Exception {
        //Given
        Category category = createTestCategory();
        CategoryDto expected = createTestCategoryDto(category);
        //When
        MvcResult result = mockMvc.perform(
                get("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new category")
    @Sql(scripts = {
            "classpath:database/categories/delete-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void createCategory_ValidRequestDto_ShouldCreateCategory() throws Exception {
        //Given
        Category category = createTestCategory();
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto(category);
        CategoryDto expected = createTestCategoryDto(category);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                post("/categories")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update category by id")
    @Sql(scripts = {
            "classpath:database/categories/add-category.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/categories/delete-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateCategory_ValidRequestDto_ShouldUpdateCategory() throws Exception {
        //Given
        Category category = createTestCategory();
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto(category);
        CategoryDto expected = createTestCategoryDto(category);
        requestDto.setName("Updated category");
        expected.setName("Updated category");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                put("/categories/1")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete category by id")
    @Sql(scripts = {
            "classpath:database/categories/add-two-categories.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/categories/delete-categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void deleteCategory_GivenCategories_ShouldDeleteCategory() throws Exception {
        //Given
        List<CategoryDto> expected = fillExpectedCategoryDtoList();
        expected.remove(1);
        //When
        mockMvc.perform(
                delete("/categories/2")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        MvcResult result = mockMvc.perform(
                get("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class
        );
        assertEquals(1, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Get a list of books by category")
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
    public void getBooksByCategory_GivenBooksWithCategory_ShouldReturnBookWithCategory()
            throws Exception {
        //Given
        List<BookDto> expected = fillExpectedBookDtoList();
        expected.get(0).setCategoryIds(Set.of(1L));
        expected.remove(1);
        expected.get(1).setCategoryIds(Set.of(1L));
        //When
        MvcResult result = mockMvc.perform(
                get("/categories/1/books")
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
