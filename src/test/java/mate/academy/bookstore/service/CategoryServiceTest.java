package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategory;
import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategoryDto;
import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategoryRequestDto;
import static mate.academy.bookstore.util.CategoryTestUtil.createTestCategoryWithOnlyMandatoryFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CategoryMapper;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.category.CategoryRepository;
import mate.academy.bookstore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Verify that correct CategoryDto was returned when calling save() method")
    public void save_WithValidRequestDto_ShouldReturnValidCategoryDto() {
        //Given
        Category category = createTestCategory();
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto(category);
        CategoryDto expected = createTestCategoryDto(category);

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        CategoryDto actual = categoryService.save(requestDto);
        //Then
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toModel(requestDto);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify that correct CategoryDto was returned when calling save() method using
            requestDto with only mandatory fields
            """)
    public void save_WithValidRequestDtoWithoutOptionalFields_ShouldReturnValidCategoryDto() {
        //Given
        Category category = createTestCategoryWithOnlyMandatoryFields();
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto(category);
        CategoryDto expected = createTestCategoryDto(category);

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        CategoryDto actual = categoryService.save(requestDto);
        //Then
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toModel(requestDto);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify that correct CategoryDto was returned when calling update() method")
    public void update_WithValidRequestDto_ShouldReturnValidCategoryDto() {
        //Given
        Category category = createTestCategory();
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto(category);
        CategoryDto expected = createTestCategoryDto(category);
        category.setName("Updated name");
        requestDto.setName("Updated name");
        expected.setName("Updated name");
        Long id = category.getId();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.updateFromRequest(requestDto, category)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        CategoryDto actual = categoryService.update(id, requestDto);
        //Then
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).updateFromRequest(requestDto, category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify that exception is thrown when calling update() method with invalid id")
    public void update_WithInvalidId_ShouldThrowException() {
        //Given
        Category category = createTestCategory();
        CreateCategoryRequestDto requestDto = createTestCategoryRequestDto(category);
        requestDto.setName("Updated name");
        Long id = 2L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(id, requestDto)
        );
        //Then
        String expected = "Can't find existing category by id: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify that correct CategoryDto was returned when calling getById() method")
    public void getById_WithValidId_ShouldReturnValidCategoryDto() {
        //Given
        Category category = createTestCategory();
        CategoryDto expected = createTestCategoryDto(category);
        Long id = category.getId();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        CategoryDto actual = categoryService.getById(id);
        //Then
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify that exception is thrown when calling getById() method with invalid id")
    public void getById_WithInvalidId_ShouldThrowException() {
        //Given
        Category category = createTestCategory();
        Long id = 2L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(id)
        );
        //Then
        String expected = "Can't get category by id: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }
}
