package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestTest {

    @Test
    void testSetId() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        assertEquals(1L, itemRequest.getId(), "ID не был установлен корректно");
    }

    @Test
    void testSetDescription() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Test description");
        assertEquals("Test description", itemRequest.getDescription(), "Описание не было установлено корректно");
    }

    @Test
    void testSetRequestor() {
        ItemRequest itemRequest = new ItemRequest();
        User user = new User(1L, "John Doe", "john.doe@example.com");
        itemRequest.setRequestor(user);
        assertEquals(user, itemRequest.getRequestor(), "Пользователь не был установлен корректно");
    }

    @Test
    void testSetCreated() {
        ItemRequest itemRequest = new ItemRequest();
        LocalDateTime createdDate = LocalDateTime.now();
        itemRequest.setCreated(createdDate);
        assertEquals(createdDate, itemRequest.getCreated(), "Дата создания не была установлена корректно");
    }

    @Test
    void testSetItems() {
        ItemRequest itemRequest = new ItemRequest();
        Item item = new Item();
        itemRequest.setItems(Collections.singletonList(item));
        assertEquals(Collections.singletonList(item), itemRequest.getItems(), "Список вещей не был установлен корректно");
    }
}
