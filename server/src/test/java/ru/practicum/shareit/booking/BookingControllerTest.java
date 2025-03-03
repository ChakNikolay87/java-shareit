package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.HeaderConstants.X_SHARER_USER_ID;

public class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, null, null, null, null, null, 1L, 1L);

        when(bookingService.createBooking(any(), any())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService, times(1)).createBooking(any(), any());
    }

    @Test
    public void approveBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, null, null, null, null, null, 1L, 1L);

        when(bookingService.approveBooking(any(), any(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService, times(1)).approveBooking(any(), any(), anyBoolean());
    }

    @Test
    public void getBookingByIdTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, null, null, null, null, null, 1L, 1L);

        when(bookingService.getBookingById(any(), any())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService, times(1)).getBookingById(any(), any());
    }

    @Test
    public void getAllBookingsTest() throws Exception {
        BookingDto bookingDto1 = new BookingDto(1L, null, null, null, null, null, 1L, 1L);
        BookingDto bookingDto2 = new BookingDto(2L, null, null, null, null, null, 2L, 2L);
        List<BookingDto> bookings = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.getAllBookings(any(), any())).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(bookingService, times(1)).getAllBookings(any(), any());
    }

    @Test
    public void getBookingsForOwnerTest() throws Exception {
        BookingDto bookingDto1 = new BookingDto(1L, null, null, null, null, null, 1L, 1L);
        BookingDto bookingDto2 = new BookingDto(2L, null, null, null, null, null, 2L, 2L);
        List<BookingDto> bookings = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.getBookingsForOwner(any(), any())).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(bookingService, times(1)).getBookingsForOwner(any(), any());
    }
}