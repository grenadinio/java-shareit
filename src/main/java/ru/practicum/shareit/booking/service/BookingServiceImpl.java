package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingMapper bookingMapper;

    @Override
    public Booking getById(Long id, Long userId) {
        findUserById(userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено брони с ID = " + id));
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("У вас не найдено такой брони или предмета.");
        }
        return booking;
    }

    @Override
    public List<Booking> getByUser(Long userId, String state) {
        findUserById(userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository
                    .findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, now, now);
            case "PAST" -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("WAITING"));
            case "REJECTED" ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("REJECTED"));
            case "ALL" -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            default -> throw new UnsupportedStateException("Unknown state: " + state);
        };
    }

    @Override
    public List<Booking> getByOwner(Long userId, String state) {
        findUserById(userId);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository
                    .findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, now, now);
            case "PAST" -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("WAITING"));
            case "REJECTED" ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf("REJECTED"));
            case "ALL" -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            default -> throw new UnsupportedStateException("Unknown state: " + state);
        };
    }

    @Override
    @Transactional
    public Booking create(BookingDto booking, Long userId) {
        booking.setBookerId(userId);
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Не найдено предмета с ID = " + booking.getItemId()));
        User user = findUserById(booking.getBookerId());
        if (!item.isAvailable()) {
            throw new AvailabilityException("Данный предмет нельзя забронировать");
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("У вас не найдено такой брони.");
        }
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(bookingMapper.toBooking(booking, user, item));
    }

    @Override
    @Transactional
    public Booking approve(Long id, boolean approved, Long userId) {
        findUserById(userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено брони с ID = " + id));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Вы не являетесь владельцем данного предмета!");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new StatusException("Нельзя подтвердить бронь, которая уже подтверждена.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найдено пользователя с id: " + userId));
    }
}
