package mate.academy.bookstore.mapper;

import java.util.List;
import mate.academy.bookstore.config.MapperConfig;
import mate.academy.bookstore.dto.BookDto;
import mate.academy.bookstore.dto.CreateBookRequestDto;
import mate.academy.bookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toBookDto(Book book);

    Book toBookModel(CreateBookRequestDto requestDto);

    List<BookDto> toBookDtoList(List<Book> books);
}
