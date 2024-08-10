package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadDateException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class BookingValidationService {
    public void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        if (start == null || end == null) {
            throw new BadDateException("Дата не может быть пустой");
        }

        if (start.isBefore(now)) {
            throw new BadDateException("Время начала не должно быть в прошлом");
        }

        if (end.isBefore(now)) {
            throw new BadDateException("Время окончания не должно быть в прошлом");
        }

        if (!start.isBefore(end)) {
            throw new BadDateException("Дата начала бронирования должна быть до даты окончания бронирования!");
        }
    }
}
