package mate.academy.bookstore.util;

import java.util.ArrayList;
import java.util.List;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.model.Category;

public class CategoryTestUtil {
    public static Category createTestCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Name");
        category.setDescription("Description");
        return category;
    }

    public static CreateCategoryRequestDto createTestCategoryRequestDto(Category category) {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(category.getName());
        requestDto.setDescription(category.getDescription());
        return requestDto;
    }

    public static CategoryDto createTestCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        return categoryDto;
    }

    public static Category createTestCategoryWithOnlyMandatoryFields() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Name");
        return category;
    }

    public static List<CategoryDto> fillExpectedCategoryDtoList() {
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto().setId(1L).setName("Category 1")
                .setDescription("Description 1"));
        expected.add(new CategoryDto().setId(2L).setName("Category 2")
                .setDescription("Description 2"));
        return expected;
    }
}
