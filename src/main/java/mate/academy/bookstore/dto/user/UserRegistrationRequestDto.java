package mate.academy.bookstore.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import mate.academy.bookstore.annotation.FieldMatch;

@Data
@FieldMatch(firstField = "password", secondField = "repeatPassword",
        message = "Passwords do not match")
public class UserRegistrationRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
