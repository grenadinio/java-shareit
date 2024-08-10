package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private final BookingValidationService bookingValidationService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getById(id, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getByUser(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getByOwner(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody BookingDto booking, @RequestHeader("X-Sharer-User-Id") Long userId) {
        bookingValidationService.validateBookingDates(booking.getStart(), booking.getEnd());
        return bookingClient.create(booking, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approve(@PathVariable Long id, @RequestParam boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.approve(id, approved, userId);
    }
}
