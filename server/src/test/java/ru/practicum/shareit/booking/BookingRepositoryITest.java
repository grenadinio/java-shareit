package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.dto.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.dto.BookingStatus.WAITING;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryITest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    private Item item;
    private User booker;
    private User owner;

    @BeforeEach
    void setUp() {
        booker = createUser("Booker", "booker@mail.ru");
        owner = createUser("Owner", "owner@mail.ru");
        ItemRequest newItemRequest = new ItemRequest();
        newItemRequest.setDescription("2345");
        newItemRequest.setRequester(booker);
        newItemRequest.setCreated(Instant.now());
        ItemRequest itemRequest = itemRequestRepository.save(newItemRequest);

        item = createItem("Item", "Description", true, owner, itemRequest);
    }

    private Item createItem(String name, String description, boolean available, User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        return itemRepository.save(item);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Booking createBooking(BookingStatus status, Item item, User booker, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setStatus(status);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        return bookingRepository.save(booking);
    }

    @Test
    void findBookingById() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 9, 12, 0);
        LocalDateTime end = start.plusMinutes(30);
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, start, end);

        Optional<Booking> result = bookingRepository.findById(booking.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getId(), result.get().getId());
    }

    @Test
    void findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.minusMinutes(10), now.plusMinutes(30));

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(booker.getId(), now, now);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.minusMinutes(10), now.minusMinutes(5));

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), now);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), now);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByBookerIdAndStatusWaitingOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.WAITING, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), WAITING);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByBookerIdAndStatusRejectedOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.REJECTED, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), REJECTED);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByBookerIdOrderByStartDesc(booker.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }


    @Test
    void findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.minusMinutes(10), now.plusMinutes(30));

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(owner.getId(), now, now);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.minusMinutes(10), now.minusMinutes(5));

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(owner.getId(), now);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(owner.getId(), now);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByItemOwnerIdAndStatusWaitingOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.WAITING, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), WAITING);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByItemOwnerIdAndStatusRejectedOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.REJECTED, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), REJECTED);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdOrderByStartDesc(owner.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findUpcomingBookingsByItemId() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.plusMinutes(10), now.plusMinutes(15));

        List<Booking> result = bookingRepository
                .findUpcomingBookingsByItemId(item.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

    @Test
    void findLastBookingsByItemId() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = createBooking(BookingStatus.APPROVED, item, booker, now.minusMinutes(10), now.minusMinutes(5));

        List<Booking> result = bookingRepository
                .findLastBookingsByItemId(item.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking.getId(), result.getFirst().getId());
        Assertions.assertEquals(booking.getItem().getOwner(), result.getFirst().getItem().getOwner());
        Assertions.assertEquals(booking.getStart(), result.getFirst().getStart());
    }

}
