package mate.academy.bookstore.service;

import java.util.List;
import mate.academy.bookstore.dto.BookDto;
import mate.academy.bookstore.dto.BookSearchParameters;
import mate.academy.bookstore.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    List<BookDto> findAll();

    List<BookDto> search(BookSearchParameters searchParameters);

    void deleteById(Long id);
}
