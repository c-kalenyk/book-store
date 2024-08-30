package mate.academy.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}
