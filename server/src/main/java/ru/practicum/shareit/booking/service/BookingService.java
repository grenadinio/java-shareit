package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {

    Booking getById(Long id, Long userId);

    List<Booking> getByUser(Long userId, String state);

    List<Booking> getByOwner(Long userId, String state);

    @Transactional
    Booking create(BookingDto booking, Long userId);

    @Transactional
    Booking approve(Long id, boolean approved, Long userId);
}
