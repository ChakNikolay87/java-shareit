package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item name", "Item description", true, null, Collections.emptyList());

        when(itemService.createItem(any(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item name"));

        verify(itemService, times(1)).createItem(any(), any());
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Updated Item", "Updated description", true, null, Collections.emptyList());

        when(itemService.updateItem(any(), any(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));

        verify(itemService, times(1)).updateItem(any(), any(), any());
    }

    @Test
    public void getItemByIdTest() throws Exception {
        ItemWithDateDto itemWithDateDto = new ItemWithDateDto(1L, "Item name", "Item description", true, null, null, Collections.emptyList());

        when(itemService.getItemById(any(), any())).thenReturn(itemWithDateDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item name"));

        verify(itemService, times(1)).getItemById(any(), any());
    }

    @Test
    public void getItemsByOwnerTest() throws Exception {
        ItemWithDateDto itemWithDateDto = new ItemWithDateDto(1L, "Item name", "Item description", true, null, null, Collections.emptyList());

        when(itemService.getItemsByOwner(any())).thenReturn(Collections.singletonList(itemWithDateDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item name"));

        verify(itemService, times(1)).getItemsByOwner(any());
    }

    @Test
    public void searchItemsTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Item name", "Item description", true, null, Collections.emptyList());

        when(itemService.searchItems(any())).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item name"));

        verify(itemService, times(1)).searchItems(any());
    }

    @Test
    public void addCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Item comment", 1L, "User", null);

        when(itemService.addComment(any(), any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Item comment"));

        verify(itemService, times(1)).addComment(any(), any(), any());
    }
}