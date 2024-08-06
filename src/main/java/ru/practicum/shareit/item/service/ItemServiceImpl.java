package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    public ItemBookingDto get(Long id, Long userId) {
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemId(id);
        BookingDto lastBooking = lastBookings.isEmpty() ? null : bookingMapper
                .toBookingDto(lastBookings.getFirst());

        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookingsByItemId(id);
        BookingDto nextBooking = upcomingBookings.isEmpty() ? null : bookingMapper
                .toBookingDto(upcomingBookings.getFirst());

        List<CommentDto> comments = commentRepository.findAllByItemId(id).stream()
                .map(commentMapper::toCommentDto).collect(Collectors.toList());

        return itemMapper.toItemBookingDto(findItemById(id), lastBooking, nextBooking, userId, comments);
    }

    public Collection<ItemBookingDto> getAllByUser(Long userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(item -> get(item.getId(), userId))
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) return Collections.emptyList();
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = findUserById(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto edit(Long itemId, Long userId, Map<String, Object> updates) {
        User user = findUserById(userId);
        Item item = findItemById(itemId);

        if (item.getOwner().equals(user)) {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        item.setName((String) value);
                        break;
                    case "description":
                        item.setDescription((String) value);
                        break;
                    case "available":
                        item.setAvailable((boolean) value);
                        break;
                }
            });
        } else {
            throw new NotOwnerException("Вы не являетесь владельцем предмета с id: " + itemId);
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto comment(Long itemId, Long userId, Comment comment) {
        Item item = findItemById(itemId);
        User user = findUserById(userId);
        List<Booking> endedBookings = bookingRepository.findLastBookingsByItemId(itemId);
        String text = comment.getText();
        if (text == null || text.isEmpty()) {
            throw new CommentException("Комментарий не может быть пустым");
        }
        for (Booking booking : endedBookings) {
            if (booking.getBooker().getId().equals(userId)) {
                return commentMapper.toCommentDto(
                        commentRepository.save(new Comment(0L, text, item, user, LocalDateTime.now())));
            }
        }
        throw new CommentException("Вы не можете оставить отзыв на данный предмет.");
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найдено пользователя с id: " + userId));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдено предмета с id: " + itemId));
    }
}
