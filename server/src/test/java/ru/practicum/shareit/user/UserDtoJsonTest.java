package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUserDtoSerialization() throws Exception {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains(
                "\"id\":1",
                "\"name\":\"John Doe\"",
                "\"email\":\"john.doe@example.com\""
        );
    }

    @Test
    public void testUserDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("John Doe");
        assertThat(userDto.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testEqualsSameValues() {
        UserDto userDto1 = new UserDto(1L, "John Doe", "john.doe@example.com");
        UserDto userDto2 = new UserDto(1L, "John Doe", "john.doe@example.com");

        assertThat(userDto1).isEqualTo(userDto2);
    }

    @Test
    public void testEqualsDifferentValues() {
        UserDto userDto1 = new UserDto(1L, "John Doe", "john.doe@example.com");
        UserDto userDto2 = new UserDto(2L, "Jane Doe", "jane.doe@example.com");

        assertThat(userDto1).isNotEqualTo(userDto2);
    }

    @Test
    public void testEqualsNull() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        assertThat(userDto).isNotEqualTo(null);
    }

    @Test
    public void testEqualsDifferentClass() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        assertThat(userDto).isNotEqualTo(new Object());
    }

    @Test
    public void testHashCodeSameValues() {
        UserDto userDto1 = new UserDto(1L, "John Doe", "john.doe@example.com");
        UserDto userDto2 = new UserDto(1L, "John Doe", "john.doe@example.com");

        assertThat(userDto1.hashCode()).isEqualTo(userDto2.hashCode());
    }

    @Test
    public void testHashCodeDifferentValues() {
        UserDto userDto1 = new UserDto(1L, "John Doe", "john.doe@example.com");
        UserDto userDto2 = new UserDto(2L, "Jane Doe", "jane.doe@example.com");

        assertThat(userDto1.hashCode()).isNotEqualTo(userDto2.hashCode());
    }

    @Test
    public void testSetId() {
        User user = new User();
        user.setId(1L);
        assertThat(user.getId()).isEqualTo(1L);
    }

}
