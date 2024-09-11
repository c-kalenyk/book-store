package mate.academy.bookstore.service;

import static mate.academy.bookstore.util.UserTestUtil.createTestUser;
import static mate.academy.bookstore.util.UserTestUtil.createTestUserRegistrationRequestDto;
import static mate.academy.bookstore.util.UserTestUtil.createTestUserResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.exception.RegistrationException;
import mate.academy.bookstore.mapper.UserMapper;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.role.RoleRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify that correct UserResponseDto was returned when calling register() method")
    public void register_WithValidRequestDto_ShouldReturnValidUserResponseDto()
            throws RegistrationException {
        //Given
        User user = createTestUser();
        UserRegistrationRequestDto requestDto = createTestUserRegistrationRequestDto(user);
        final UserResponseDto expected = createTestUserResponseDto(user);
        final Role role = user.getRoles().iterator().next();
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(Role.RoleName.ROLE_USER)).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);
        //When
        UserResponseDto actual = userService.register(requestDto);
        //Then
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(userMapper, times(1)).toModel(requestDto);
        verify(passwordEncoder, times(1)).encode(requestDto.getPassword());
        verify(roleRepository, times(1)).findByRoleName(Role.RoleName.ROLE_USER);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder, roleRepository);
    }

    @Test
    @DisplayName("""
    Verify that exception is thrown when calling register() method
    and user with such email already exists""")
    public void register_WithExistingUser_ShouldThrowException() {
        //Given
        User existingUser = createTestUser();
        UserRegistrationRequestDto requestDto = createTestUserRegistrationRequestDto(existingUser);

        when(userRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(existingUser));
        //When
        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(requestDto)
        );
        //Then
        String expected = "Can't register new user. User with this email already exists";
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository);
    }
}
