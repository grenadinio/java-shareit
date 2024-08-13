package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerITest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getBookingsById() throws Exception {
        long bookingId = 1L;
        long userId = 1L;

        when(bookingService.getById(bookingId, userId)).thenReturn(new Booking());

        mockMvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(bookingService, times(1)).getById(bookingId, userId);
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getBookingsByUser(BookingState state) throws Exception {
        long userId = 2L;
        when(bookingService.getByUser(eq(userId), eq(state.name())))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).getByUser(eq(userId), eq(state.name()));
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getBookingsByOwner(BookingState state) throws Exception {
        long userId = 3L;
        when(bookingService.getByUser(eq(userId), eq(state.name())))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).getByOwner(eq(userId), eq(state.name()));
    }

    @Test
    void createBooking() throws Exception {
        long userId = 4L;
        long itemId = 1L;
        LocalDateTime start = LocalDateTime.of(2024, 8, 9, 12, 0);
        LocalDateTime end = start.plusMinutes(30);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String bookingJson = objectMapper.writeValueAsString(bookingDto);

        when(bookingService.create(bookingDto, userId)).thenReturn(new Booking());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(bookingService, times(1)).create(eq(bookingDto), eq(userId));
    }

    @Test
    void approveBooking() throws Exception {
        long userId = 5L;
        long bookingId = 2L;
        boolean approved = true;

        when(bookingService.approve(bookingId, approved, userId)).thenReturn(new Booking());

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(bookingService, times(1)).approve(bookingId, approved, userId);
    }
}

