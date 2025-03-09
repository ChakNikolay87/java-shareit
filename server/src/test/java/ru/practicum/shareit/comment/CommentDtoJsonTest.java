package ru.practicum.shareit.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCommentDtoSerialization() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Great item!", 1L, "User1", LocalDateTime.now());

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains(
                "\"id\":1",
                "\"text\":\"Great item!\"",
                "\"itemId\":1",
                "\"authorName\":\"User1\"",
                "\"created\""
        );
    }


    @Test
    public void testCommentDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"text\":\"Great item!\",\"itemId\":1,\"authorName\":\"User1\",\"created\":\"2025-03-03T12:00:00\"}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Great item!");
        assertThat(commentDto.getItemId()).isEqualTo(1L);
        assertThat(commentDto.getAuthorName()).isEqualTo("User1");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.parse("2025-03-03T12:00:00"));
    }

    @Test
    public void testEqualsSameObjectShouldReturnTrue() {
        CommentDto commentDto1 = new CommentDto(1L, "text", 1L, "author", LocalDateTime.now());
        CommentDto commentDto2 = commentDto1;

        assertTrue(commentDto1.equals(commentDto2));
    }

    @Test
    public void testEqualsDifferentObjectsWithSameFieldsShouldReturnTrue() {
        CommentDto commentDto1 = new CommentDto(1L, "text", 1L, "author", LocalDateTime.now());
        CommentDto commentDto2 = new CommentDto(1L, "text", 1L, "author", commentDto1.getCreated());

        assertTrue(commentDto1.equals(commentDto2));
    }

    @Test
    public void testEqualsDifferentObjectsWithDifferentFieldsShouldReturnFalse() {
        CommentDto commentDto1 = new CommentDto(1L, "text1", 1L, "author1", LocalDateTime.now());
        CommentDto commentDto2 = new CommentDto(2L, "text2", 2L, "author2", LocalDateTime.now().minusDays(1));

        assertFalse(commentDto1.equals(commentDto2));
    }

    @Test
    public void testEqualsNullObjectShouldReturnFalse() {
        CommentDto commentDto1 = new CommentDto(1L, "text", 1L, "author", LocalDateTime.now());

        assertFalse(commentDto1.equals(null));
    }

    @Test
    public void testEqualsDifferentClassObjectShouldReturnFalse() {
        CommentDto commentDto1 = new CommentDto(1L, "text", 1L, "author", LocalDateTime.now());
        String otherObject = "Not a CommentDto";

        assertFalse(commentDto1.equals(otherObject));
    }

    @Test
    public void testHashCodeSameObjectShouldReturnSameHashCode() {
        CommentDto commentDto1 = new CommentDto(1L, "text", 1L, "author", LocalDateTime.now());
        CommentDto commentDto2 = new CommentDto(1L, "text", 1L, "author", commentDto1.getCreated());

        assertEquals(commentDto1.hashCode(), commentDto2.hashCode());
    }

    @Test
    public void testHashCodeDifferentObjectsShouldReturnDifferentHashCodes() {
        CommentDto commentDto1 = new CommentDto(1L, "text1", 1L, "author1", LocalDateTime.now());
        CommentDto commentDto2 = new CommentDto(2L, "text2", 2L, "author2", LocalDateTime.now().minusDays(1));

        assertNotEquals(commentDto1.hashCode(), commentDto2.hashCode());
    }
}
