package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public Booking getById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(id, userId);
    }

    @GetMapping()
    public List<Booking> getByUser(@RequestParam(defaultValue = "ALL") String state,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getByOwner(@RequestParam(defaultValue = "ALL") String state,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getByOwner(userId, state);
    }

    @PostMapping
    public Booking create(@RequestBody BookingDto booking, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{id}")
    public Booking approve(@PathVariable Long id, @RequestParam boolean approved,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approve(id, approved, userId);
    }
}
