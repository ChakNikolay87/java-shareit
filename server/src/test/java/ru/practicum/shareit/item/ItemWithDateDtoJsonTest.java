package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithDateDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithDateDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testItemWithDateDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"ItemName\",\"description\":\"ItemDescription\",\"available\":true,\"lastBooking\":\"2025-03-03T12:00:00\",\"nextBooking\":\"2025-03-04T12:00:00\"}";
        ItemWithDateDto itemWithDateDto = objectMapper.readValue(json, ItemWithDateDto.class);

        assertThat(itemWithDateDto.getId()).isEqualTo(1L);
        assertThat(itemWithDateDto.getName()).isEqualTo("ItemName");
        assertThat(itemWithDateDto.getDescription()).isEqualTo("ItemDescription");
        assertThat(itemWithDateDto.getAvailable()).isTrue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        assertThat(itemWithDateDto.getLastBooking()).isEqualTo(LocalDateTime.parse("2025-03-03T12:00:00", formatter));
        assertThat(itemWithDateDto.getNextBooking()).isEqualTo(LocalDateTime.parse("2025-03-04T12:00:00", formatter));
        assertThat(itemWithDateDto.getComments()).isNull();
    }

    @Test
    public void testSetComments() {
        ItemWithDateDto itemWithDateDto = new ItemWithDateDto();
        CommentDto commentDto = new CommentDto();
        commentDto.setText("This is a comment");

        itemWithDateDto.setComments(Collections.singletonList(commentDto));
        assertThat(itemWithDateDto.getComments()).isNotNull();
        assertThat(itemWithDateDto.getComments().size()).isEqualTo(1);
        assertThat(itemWithDateDto.getComments().get(0).getText()).isEqualTo("This is a comment");
    }

//    @Test
//    public void testEqualsAndHashCode() {
//        ItemWithDateDto item1 = new ItemWithDateDto(1L, "ItemName", "ItemDescription", true, LocalDateTime.now(), LocalDateTime.now().plusDays(1), Collections.emptyList());
//        ItemWithDateDto item2 = new ItemWithDateDto(1L, "ItemName", "ItemDescription", true, LocalDateTime.now(), LocalDateTime.now().plusDays(1), Collections.emptyList());
//
//        assertThat(item1).isEqualTo(item2);
//
//        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
//    }
}
