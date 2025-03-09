package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDtoShouldConvertUserToUserDto() {
        User user = new User(1L, "John Doe", "john.doe@example.com");
        UserDto userDto = UserMapper.toUserDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUserShouldConvertUserDtoToUser() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        User user = UserMapper.toUser(userDto);

        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void userMapperShouldNotBeInstantiated() {
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            var constructor = UserMapper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertTrue(exception.getCause().getMessage().contains("Utility class"));
    }

}
