package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ItemWithDateMapperTest {

    @Test
    public void testToItemDto() {
        User user = mock(User.class);
        Item item = new Item(
                1L,
                "ItemName",
                "ItemDescription",
                true,
                user,
                null,
                List.of()
        );

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("ItemName");
        assertThat(itemDto.getDescription()).isEqualTo("ItemDescription");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
        assertThat(itemDto.getComments()).isEmpty();
    }

    @Test
    public void testToItemDto_withRequest() {
        User user = mock(User.class);
        ItemRequest itemRequest = mock(ItemRequest.class);
        Item item = new Item(
                1L,
                "ItemName",
                "ItemDescription",
                true,
                user,
                itemRequest,
                List.of()
        );

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getRequestId()).isNotNull();
    }


    @Test
    public void testToItem() {
        User owner = mock(User.class);
        ItemRequest itemRequest = mock(ItemRequest.class);
        ItemDto itemDto = new ItemDto(
                1L,
                "ItemName",
                "ItemDescription",
                true,
                null,
                List.of(new CommentDto(1L, "Great item", 1L, "User", LocalDateTime.parse("2025-03-05T12:00:00")))
        );

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("ItemName");
        assertThat(item.getDescription()).isEqualTo("ItemDescription");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(itemRequest);
        assertThat(item.getComments()).isEmpty();
    }

    @Test
    public void testToItem_withEmptyComments() {
        User owner = mock(User.class);
        ItemRequest itemRequest = mock(ItemRequest.class);
        ItemDto itemDto = new ItemDto(
                1L,
                "ItemName",
                "ItemDescription",
                true,
                null,
                Collections.emptyList()
        );

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("ItemName");
        assertThat(item.getDescription()).isEqualTo("ItemDescription");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(itemRequest);
        assertThat(item.getComments()).isEmpty();
    }
}
