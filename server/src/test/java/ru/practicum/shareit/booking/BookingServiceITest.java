package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceITest {
    private BookingServiceImpl bookingService;

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Item ownedItem;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        BookingMapper bookingMapper = new BookingMapper(itemRepository, userRepository);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper);
        user = new User(1L, "User", "user@mail.ru");
        item = new Item(1L, "Item", "Description", true, null, null);
        item.setOwner(new User(2L, "User2", "user2@mail.ru"));
        ownedItem = new Item(2L, "Item2", "Description2", true, user, null);
    }

    @Test
    void getById() {
        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getById(bookingId, user.getId());
        Assertions.assertNotNull(result);
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getByIdByOwner() {
        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        user.setId(2L);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getById(bookingId, 1L);
        });

        assertEquals("У вас не найдено такой брони или предмета.", exception.getMessage());
        verify(bookingRepository, times(1)).findById(any());
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getByUser(BookingState state) throws Exception {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(eq(user.getId()), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(eq(user.getId())))
                .thenReturn(Collections.emptyList());

        List<Booking> result = bookingService.getByUser(user.getId(), state.name());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getByUserWithUnsupportedState() throws Exception {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(UnsupportedStateException.class, () -> {
            bookingService.getByUser(user.getId(), "SOMEUNSUPPORTEDSTATE");
        });

        assertEquals("Неизвестное состояние: SOMEUNSUPPORTEDSTATE", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getByOwner(BookingState state) throws Exception {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(eq(user.getId()), any(), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(user.getId())))
                .thenReturn(Collections.emptyList());

        List<Booking> result = bookingService.getByOwner(user.getId(), state.name());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void createBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(2L);
        bookingDto.setBookerId(user.getId());
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        bookingDto.setEnd(bookingDto.getStart().plusMinutes(30));

        Booking result = bookingService.create(bookingDto, user.getId());

        Assertions.assertNotNull(result);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBookingWithUnavailableItem() {
        item.setAvailable(false);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(2L);
        bookingDto.setBookerId(user.getId());
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        bookingDto.setEnd(bookingDto.getStart().plusMinutes(30));

        Exception exception = assertThrows(AvailabilityException.class, () -> {
            bookingService.create(bookingDto, user.getId());
        });

        assertEquals("Данный предмет нельзя забронировать", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    void approveBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(ownedItem);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(ownedItem.getId())).thenReturn(Optional.of(ownedItem));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.approve(booking.getId(), true, user.getId());

        Assertions.assertNotNull(result);
        verify(bookingRepository, times(1)).save(any());

    }

    @Test
    void approveApprovedBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(ownedItem);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(ownedItem.getId())).thenReturn(Optional.of(ownedItem));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        Exception exception = assertThrows(StatusException.class, () -> {
            bookingService.approve(booking.getId(), true, user.getId());
        });

        assertEquals("Нельзя подтвердить бронь, которая уже подтверждена.", exception.getMessage());
        verify(bookingRepository, times(0)).save(any());

    }
}
