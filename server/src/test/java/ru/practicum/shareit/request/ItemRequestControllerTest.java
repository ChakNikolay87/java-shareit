package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createItemRequestTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Request Description", 1L,
                null, null);
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Request Description"));

        verify(itemRequestService, times(1)).createItemRequest(anyLong(),
                any(ItemRequestDto.class));
    }

    @Test
    public void getItemRequestsTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Request Description", 1L,
                null, null);
        List<ItemRequestDto> itemRequests = Arrays.asList(itemRequestDto);
        when(itemRequestService.getItemRequestsByUser(anyLong())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request Description"));

        verify(itemRequestService, times(1)).getItemRequestsByUser(anyLong());
    }

    @Test
    public void getAllItemRequestsTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Request Description", 1L,
                null, null);
        List<ItemRequestDto> itemRequests = Arrays.asList(itemRequestDto);
        when(itemRequestService.getAllItemRequests(anyLong())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Request Description"));

        verify(itemRequestService, times(1)).getAllItemRequests(anyLong());
    }

    @Test
    public void getItemRequestByIdTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Request Description", 1L,
                null, null);
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Request Description"));

        verify(itemRequestService, times(1)).getItemRequestById(anyLong(), anyLong());
    }
}