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

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;
    private final BookingValidationService bookingValidationService;

    @GetMapping("/{id}")
    public Object getById(@PathVariable Long id, @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.getById(id, userId);
    }

    @GetMapping
    public Object getByUser(@RequestParam(defaultValue = "ALL") String state,
                            @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.getByUser(userId, state);
    }

    @GetMapping("/owner")
    public Object getByOwner(@RequestParam(defaultValue = "ALL") String state,
                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.getByOwner(userId, state);
    }

    @PostMapping
    public Object create(@RequestBody BookingDto booking, @RequestHeader(USER_ID_HEADER) Long userId) {
        bookingValidationService.validateBookingDates(booking.getStart(), booking.getEnd());
        return bookingClient.create(booking, userId);
    }

    @PatchMapping("/{id}")
    public Object approve(@PathVariable Long id, @RequestParam boolean approved,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.approve(id, approved, userId);
    }
}
