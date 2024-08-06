package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@AllArgsConstructor
public class BookingMapper {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public Booking toBooking(BookingDto bookingDTO, User user, Item item) {
        return new Booking(
                bookingDTO.getId(),
                bookingDTO.getStart(),
                bookingDTO.getEnd(),
                item,
                user,
                bookingDTO.getStatus()
        );
    }
}
