package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.comment.model.Comment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentRequestTest {

    @Test
    void testSetId() {
        Comment comment = new Comment();
        comment.setId(1L);
        assertEquals(1L, comment.getId(), "ID не был установлен корректно");
    }

    @Test
    void testSetText() {
        Comment comment = new Comment();
        comment.setText("Test comment text");
        assertEquals("Test comment text", comment.getText(), "Текст комментария не был установлен корректно");
    }

    @Test
    void testSetItem() {
        Comment comment = new Comment();
        Item item = new Item(); // Можно добавить настройки для item, если нужно
        comment.setItem(item);
        assertEquals(item, comment.getItem(), "Предмет не был установлен корректно");
    }

    @Test
    void testSetAuthor() {
        Comment comment = new Comment();
        User author = new User(1L, "John Doe", "john.doe@example.com");
        comment.setAuthor(author);
        assertEquals(author, comment.getAuthor(), "Автор не был установлен корректно");
    }

    @Test
    void testSetCreated() {
        Comment comment = new Comment();
        LocalDateTime createdDate = LocalDateTime.now();
        comment.setCreated(createdDate);
        assertEquals(createdDate, comment.getCreated(), "Дата создания не была установлена корректно");
    }
}
