package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemServiceITest {
    private ItemServiceImpl itemService;

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        ItemMapper itemMapper = new ItemMapper();
        BookingMapper bookingMapper = new BookingMapper(itemRepository, userRepository);
        CommentMapper commentMapper = new CommentMapper();
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository, itemMapper, bookingMapper, commentMapper);

        user = new User(1L, "User", "user@mail.ru");

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2024, 8, 9, 12, 0));
        booking.setEnd(booking.getStart().plusMinutes(30));

        comment = new Comment();
        comment.setText("Text");
        comment.setCreated(LocalDateTime.of(2024, 8, 9, 12, 0));
        comment.setId(1L);
        comment.setAuthor(user);
    }

    @Test
    void getItemById() {
        long itemId = 1L;
        Item item = new Item(1L, "Item", "Description", true, user, null);
        booking.setItem(item);
        comment.setItem(item);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findUpcomingBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemBookingDto result = itemService.get(itemId, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void getItemByIdWhenBookingNull() {
        long itemId = 1L;
        Item item = new Item(1L, "Item", "Description", true, user, null);
        booking.setItem(item);
        comment.setItem(item);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findUpcomingBookingsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemBookingDto result = itemService.get(itemId, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void getAllItemsByUser() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));
        booking.setItem(item);
        comment.setItem(item);

        Collection<ItemBookingDto> result = itemService.getAllByUser(user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).findAllByOwnerId(user.getId());
    }

    @Test
    void searchItems() {
        String text = "Text";
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(itemRepository.searchAllByTextInNameOrDescription(any())).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.search(text);
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).searchAllByTextInNameOrDescription(text);
    }

    @Test
    void searchEmptyItem() {
        String text = "";
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(itemRepository.searchAllByTextInNameOrDescription(any())).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.search(text);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        verify(itemRepository, times(0)).searchAllByTextInNameOrDescription(text);

    }

    @Test
    void createItem() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(new ItemRequest()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        ItemDto result = itemService.create(itemDto, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);

    }

    @Test
    void createItemWithRequest() {
        ItemRequest itemRequest = new ItemRequest(1L, "123", user, Instant.now());
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setRequestId(1L);
        ItemDto result = itemService.create(itemDto, user.getId());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createItemWithBadRequestId() {
        ItemRequest itemRequest = new ItemRequest(1L, "123", user, Instant.now());
        Item item = new Item(1L, "Item", "Description", true, user, itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setRequestId(2L);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.create(itemDto, user.getId());
        });

        assertEquals("Не найдено запроса предмета с id: 2", exception.getMessage());
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void editItem() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.edit(item.getId(), user.getId(), new HashMap<>());
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemName() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.edit(item.getId(), user.getId(), Map.of("name", "NewItem"));
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemDescription() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.edit(item.getId(), user.getId(), Map.of("description", "description"));
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemAvailable() {
        Item item = new Item(1L, "Item", "Description", true, user, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.edit(item.getId(), user.getId(), Map.of("available", false));
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void editItemByNotOwner() {
        User owner = new User(2L, "User", "user@mail.ru");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        Exception exception = assertThrows(NotOwnerException.class, () -> {
            itemService.edit(item.getId(), 1L, Map.of("name", "name"));
        });

        assertEquals("Вы не являетесь владельцем предмета с id: 1", exception.getMessage());
        verify(itemRepository, times(0)).save(item);
    }

    @Test
    void commentItem() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Text");
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findCompletedBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.comment(item.getId(), user.getId(), commentDto);
        Assertions.assertNotNull(result);
    }

    @Test
    void commentItemWithEmptyComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        Exception exception = assertThrows(CommentException.class, () -> {
            itemService.comment(item.getId(), user.getId(), commentDto);
        });

        assertEquals("Комментарий не может быть пустым", exception.getMessage());

    }

    @Test
    void commentItemWithNullComment() {
        CommentDto commentDto = new CommentDto();
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        Exception exception = assertThrows(CommentException.class, () -> {
            itemService.comment(item.getId(), user.getId(), commentDto);
        });

        assertEquals("Комментарий не может быть пустым", exception.getMessage());

    }

    @Test
    void commentItemWithoutBooking() {
        User owner = new User(2L, "User", "user@mail.ru");
        CommentDto commentDto = new CommentDto();
        Item item = new Item(1L, "Item", "Description", true, user, null);
        comment.setItem(item);
        commentDto.setText("Text");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBookingsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.save(any())).thenReturn(comment);

        Exception exception = assertThrows(CommentException.class, () -> {
            itemService.comment(item.getId(), user.getId(), commentDto);
        });

        assertEquals("Вы не можете оставить отзыв на данный предмет.", exception.getMessage());

    }
}
