package mate.academy.bookstore.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}
