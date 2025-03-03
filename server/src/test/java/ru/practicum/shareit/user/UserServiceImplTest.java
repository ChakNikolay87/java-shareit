package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "Test User", "testuser@example.com");
    }

    @Test
    void createUserTest() {
        UserDto createdUser = userService.createUser(userDto);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());

        User userFromDb = userRepository.findById(createdUser.getId()).orElseThrow();
        assertEquals(userDto.getName(), userFromDb.getName());
        assertEquals(userDto.getEmail(), userFromDb.getEmail());
    }

    @Test
    void updateUserTest() {
        UserDto createdUser = userService.createUser(userDto);

        createdUser.setName("Updated User");
        createdUser.setEmail("updateduser@example.com");

        UserDto updatedUser = userService.updateUser(createdUser.getId(), createdUser);

        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updateduser@example.com", updatedUser.getEmail());

        User userFromDb = userRepository.findById(updatedUser.getId()).orElseThrow();
        assertEquals("Updated User", userFromDb.getName());
        assertEquals("updateduser@example.com", userFromDb.getEmail());
    }

    @Test
    void getUserByIdTest() {
        UserDto createdUser = userService.createUser(userDto);

        UserDto fetchedUser = userService.getUserById(createdUser.getId());

        assertNotNull(fetchedUser);
        assertEquals(createdUser.getId(), fetchedUser.getId());
        assertEquals(createdUser.getName(), fetchedUser.getName());
        assertEquals(createdUser.getEmail(), fetchedUser.getEmail());
    }

    @Test
    void getAllUsersTest() {
        userService.createUser(userDto);
        userService.createUser(new UserDto(null, "Another User", "anotheruser@example.com"));

        List<UserDto> users = userService.getAllUsers();

        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }

    @Test
    void deleteUserTest() {
        UserDto createdUser = userService.createUser(userDto);

        userService.deleteUser(createdUser.getId());

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(createdUser.getId()));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        userService.createUser(userDto);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
    }

    @Test
    void updateUser_WithExistingEmail_ShouldThrowException() {
        UserDto firstUser = userService.createUser(userDto);
        UserDto secondUser = userService.createUser(new UserDto(null,
                "Another User", "anotheruser@example.com"));

        secondUser.setEmail(firstUser.getEmail());
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(secondUser.getId(), secondUser));
    }
}