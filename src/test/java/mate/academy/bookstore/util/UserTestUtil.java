package mate.academy.bookstore.util;

import java.util.Set;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;

public class UserTestUtil {
    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@test.com");
        user.setPassword("password");
        user.setFirstName("Bob");
        user.setLastName("Test");
        user.setShippingAddress("St Bob street, 123");
        user.setRoles(Set.of(createAdminRole()));
        return user;
    }

    public static Role createAdminRole() {
        Role role = new Role();
        role.setId(1L);
        role.setRoleName(Role.RoleName.ROLE_ADMIN);
        return role;
    }

    public static UserRegistrationRequestDto createTestUserRegistrationRequestDto(User user) {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail(user.getEmail());
        requestDto.setPassword(user.getPassword());
        requestDto.setRepeatPassword(user.getPassword());
        requestDto.setFirstName(user.getFirstName());
        requestDto.setLastName(user.getLastName());
        requestDto.setShippingAddress(user.getShippingAddress());
        return requestDto;
    }

    public static UserResponseDto createTestUserResponseDto(User user) {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setShippingAddress(user.getShippingAddress());
        return userDto;
    }
}
