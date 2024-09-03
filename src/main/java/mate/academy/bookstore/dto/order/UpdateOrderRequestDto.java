package mate.academy.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.bookstore.model.Order;

@Data
public class UpdateOrderRequestDto {
    @NotNull
    private Order.Status status;
}
