package mate.academy.bookstore.dto;

import java.math.BigDecimal;

public record BookSearchParameters(String titlePart,
                                   String[] authors,
                                   String isbn,
                                   BigDecimal maxPrice) {
}
