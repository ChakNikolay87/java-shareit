package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUserShouldCreateUserWhenValidDto() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        UserDto createdUser = userService.createUser(userDto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("John Doe");
        assertThat(createdUser.getEmail()).isEqualTo("john.doe@example.com");

        User savedUser = userRepository.findById(createdUser.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailAlreadyExists() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Alice");
        userDto1.setEmail("alice@example.com");
        userService.createUser(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Bob");
        userDto2.setEmail("alice@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto2));
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailIsInvalid() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("invalid-email");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
    }

    @Test
    void createUserShouldThrowExceptionWhenNameIsNull() {
        UserDto userDto = new UserDto();
        userDto.setName(null);
        userDto.setEmail("john.doe@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
    }

    @Test
    void createUserShouldThrowExceptionWhenNameIsEmpty() {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("john.doe@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
    }
}
