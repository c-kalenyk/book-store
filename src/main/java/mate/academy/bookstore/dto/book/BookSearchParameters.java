package mate.academy.bookstore.dto.book;

import java.math.BigDecimal;

public record BookSearchParameters(String titlePart,
                                   String[] authors,
                                   String isbn,
                                   BigDecimal maxPrice) {
}
