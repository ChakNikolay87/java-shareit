package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testItemRequestDtoSerialization() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription", true, null, null);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Request description", 1L, LocalDateTime.now(), Collections.singletonList(itemDto));

        String json = objectMapper.writeValueAsString(itemRequestDto);

        assertThat(json).contains(
                "\"id\":1",
                "\"description\":\"Request description\"",
                "\"requestor\":1",
                "\"created\"",
                "\"items\":[" ,
                "\"id\":1",
                "\"name\":\"ItemName\"",
                "\"description\":\"ItemDescription\""
        );
    }

    @Test
    public void testItemRequestDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"description\":\"Request description\",\"requestor\":1,\"created\":\"2025-03-03T12:00:00\",\"items\":[{\"id\":1,\"name\":\"ItemName\",\"description\":\"ItemDescription\",\"available\":true}]}";

        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("Request description");
        assertThat(itemRequestDto.getRequestor()).isEqualTo(1L);
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.parse("2025-03-03T12:00:00"));
        assertThat(itemRequestDto.getItems()).hasSize(1);
        assertThat(itemRequestDto.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getItems().get(0).getName()).isEqualTo("ItemName");
    }

    @Test
    public void testEqualsSameObjectShouldReturnTrue() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description", 1L, LocalDateTime.now(), Collections.emptyList());
        ItemRequestDto itemRequestDto2 = itemRequestDto1;

        assertTrue(itemRequestDto1.equals(itemRequestDto2));
    }

    @Test
    public void testEqualsDifferentObjectsWithSameFieldsShouldReturnTrue() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description", 1L, LocalDateTime.now(), Collections.emptyList());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(1L, "description", 1L, itemRequestDto1.getCreated(), Collections.emptyList());

        assertTrue(itemRequestDto1.equals(itemRequestDto2));
    }

    @Test
    public void testEqualsDifferentObjectsWithDifferentFieldsShouldReturnFalse() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description1", 1L, LocalDateTime.now(), Collections.emptyList());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "description2", 2L, LocalDateTime.now().minusDays(1), Collections.singletonList(new ItemDto()));

        assertFalse(itemRequestDto1.equals(itemRequestDto2));
    }

    @Test
    public void testEqualsNullObjectShouldReturnFalse() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description", 1L, LocalDateTime.now(), Collections.emptyList());

        assertFalse(itemRequestDto1.equals(null));
    }

    @Test
    public void testEqualsDifferentClassObjectShouldReturnFalse() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description", 1L, LocalDateTime.now(), Collections.emptyList());
        String otherObject = "Not an ItemRequestDto";

        assertFalse(itemRequestDto1.equals(otherObject));
    }

    @Test
    public void testHashCodeSameObjectShouldReturnSameHashCode() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description", 1L, LocalDateTime.now(), Collections.emptyList());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(1L, "description", 1L, itemRequestDto1.getCreated(), Collections.emptyList());

        assertEquals(itemRequestDto1.hashCode(), itemRequestDto2.hashCode());
    }

    @Test
    public void testHashCodeDifferentObjectsShouldReturnDifferentHashCodes() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description1", 1L, LocalDateTime.now(), Collections.emptyList());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "description2", 2L, LocalDateTime.now().minusDays(1), Collections.singletonList(new ItemDto()));

        assertNotEquals(itemRequestDto1.hashCode(), itemRequestDto2.hashCode());
    }
}
