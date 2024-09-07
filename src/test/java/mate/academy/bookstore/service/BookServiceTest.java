package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.BookTestUtil.createTestBook;
import static mate.academy.bookstore.util.BookTestUtil.createTestBookDto;
import static mate.academy.bookstore.util.BookTestUtil.createTestBookRequestDto;
import static mate.academy.bookstore.util.BookTestUtil.createTestBookWithOnlyMandatoryFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.book.BookDto;
import mate.academy.bookstore.dto.book.CreateBookRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.BookMapper;
import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.book.BookRepository;
import mate.academy.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify that correct BookDto was returned when calling save() method")
    public void save_WithValidRequestDto_ShouldReturnValidBookDto() {
        //Given
        Book book = createTestBook();
        CreateBookRequestDto requestDto = createTestBookRequestDto(book);
        BookDto expected = createTestBookDto(book);

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        //When
        BookDto actual = bookService.save(requestDto);
        //Then
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify that correct BookDto was returned when calling save() method using
            requestDto with only mandatory fields
            """)
    public void save_WithValidRequestDtoWithoutOptionalFields_ShouldReturnValidBookDto() {
        //Given
        Book book = createTestBookWithOnlyMandatoryFields();
        CreateBookRequestDto requestDto = createTestBookRequestDto(book);
        BookDto expected = createTestBookDto(book);

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        //When
        BookDto actual = bookService.save(requestDto);
        //Then
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify that correct BookDto was returned when calling update() method")
    public void update_WithValidRequestDto_ShouldReturnValidBookDto() {
        //Given
        Book book = createTestBook();
        CreateBookRequestDto requestDto = createTestBookRequestDto(book);
        BookDto expected = createTestBookDto(book);
        book.setTitle("Updated title");
        requestDto.setTitle("Updated title");
        expected.setTitle("Updated title");
        Long id = book.getId();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.updateFromRequest(requestDto, book)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        //When
        BookDto actual = bookService.update(id, requestDto);
        //Then
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).updateFromRequest(requestDto, book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify that exception is thrown when calling update() method with invalid id")
    public void update_WithInvalidId_ShouldThrowException() {
        //Given
        Book book = createTestBook();
        CreateBookRequestDto requestDto = createTestBookRequestDto(book);
        requestDto.setTitle("Updated title");
        Long id = 2L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.update(id, requestDto)
        );
        //Then
        String expected = "Can't find existing book by id: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(id);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify that correct BookDto was returned when calling findById() method")
    public void findById_WithValidId_ShouldReturnValidBookDto() {
        //Given
        Book book = createTestBook();
        BookDto expected = createTestBookDto(book);
        Long id = book.getId();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);
        //When
        BookDto actual = bookService.findById(id);
        //Then
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(id);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify that exception is thrown when calling findById() method with invalid id")
    public void findById_WithInvalidId_ShouldThrowException() {
        //Given
        Book book = createTestBook();
        Long id = 2L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        //When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findById(id)
        );
        //Then
        String expected = "Can't get book by id: " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(id);
        verifyNoMoreInteractions(bookRepository);
    }
}
