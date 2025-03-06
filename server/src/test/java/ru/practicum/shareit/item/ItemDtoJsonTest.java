package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testItemDtoSerialization() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription", true, 100L, null);
        String json = objectMapper.writeValueAsString(itemDto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"ItemName\"");
        assertThat(json).contains("\"description\":\"ItemDescription\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    public void testItemDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"ItemName\",\"description\":\"ItemDescription\",\"available\":true,\"requestId\":100}";
        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("ItemName");
        assertThat(itemDto.getDescription()).isEqualTo("ItemDescription");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(100L);
    }


    @Test
    public void testNotEquals() {
        ItemDto itemDto1 = new ItemDto(1L, "ItemName", "ItemDescription", true, 100L, null);
        ItemDto itemDto2 = new ItemDto(2L, "ItemName", "ItemDescription", true, 100L, null);

        assertThat(itemDto1).isNotEqualTo(itemDto2);
    }

    @Test
    public void testSetId() {
        Item item = new Item();
        item.setId(1L);
        assertThat(item.getId()).isEqualTo(1L);
    }

    @Test
    public void testSetName() {
        Item item = new Item();
        item.setName("ItemName");
        assertThat(item.getName()).isEqualTo("ItemName");
    }

    @Test
    public void testSetDescription() {
        Item item = new Item();
        item.setDescription("ItemDescription");
        assertThat(item.getDescription()).isEqualTo("ItemDescription");
    }

    @Test
    public void testSetAvailable() {
        Item item = new Item();
        item.setAvailable(true);
        assertThat(item.getAvailable()).isTrue();
    }


    @Test
    public void testSetRequest() {
        ItemRequest request = mock(ItemRequest.class);
        Item item = new Item();
        item.setRequest(request);
        assertThat(item.getRequest()).isEqualTo(request);
    }

    @Test
    public void testSetComments() {
        Comment comment = mock(Comment.class);
        Item item = new Item();
        item.setComments(Collections.singletonList(comment));
        assertThat(item.getComments()).hasSize(1).contains(comment);
    }

    @Test
    public void testEqualsMethod() {
        ItemDto item1 = new ItemDto(1L, "ItemName", "ItemDescription", true, 100L, Collections.emptyList());
        ItemDto item2 = new ItemDto(1L, "ItemName", "ItemDescription", true, 100L, Collections.emptyList());

        assertThat(item1).isEqualTo(item2);
        assertThat(item1).isEqualTo(item1);
        assertThat(item1).isNotEqualTo(null);
        assertThat(item1).isNotEqualTo(new Object());
    }

    @Test
    public void testEqualsAndHashCode() {
        ItemDto item1 = new ItemDto(1L, "ItemName", "ItemDescription", true, 100L, Collections.emptyList());
        ItemDto item2 = new ItemDto(1L, "ItemName", "ItemDescription", true, 100L, Collections.emptyList());

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());

        assertThat(item1).isNotEqualTo(new ItemDto(2L, "ItemName", "ItemDescription", true, 100L, Collections.emptyList()));
        assertThat(item1).isNotEqualTo(new ItemDto(1L, "DifferentName", "ItemDescription", true, 100L, Collections.emptyList()));
        assertThat(item1).isNotEqualTo(new ItemDto(1L, "ItemName", "DifferentDescription", true, 100L, Collections.emptyList()));
        assertThat(item1).isNotEqualTo(new ItemDto(1L, "ItemName", "ItemDescription", false, 100L, Collections.emptyList()));
        assertThat(item1).isNotEqualTo(new ItemDto(1L, "ItemName", "ItemDescription", true, 200L, Collections.emptyList()));
        assertThat(item1).isNotEqualTo(new ItemDto(1L, "ItemName", "ItemDescription", true, 100L, List.of(new CommentDto())));
        assertThat(item1).isNotEqualTo(null);
        assertThat(item1).isNotEqualTo(new Object());
    }


}
