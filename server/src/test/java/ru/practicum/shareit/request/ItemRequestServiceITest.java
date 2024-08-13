package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ItemRequestServiceITest {
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    private User user;

    @BeforeEach
    void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        ItemMapper itemMapper = new ItemMapper();
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository,
                itemRequestMapper, itemMapper);

        user = new User(1L, "Name", "email@mail.ru");
    }

    @Test
    void getAllSelfItemRequests() {
        ItemRequest itemRequest = new ItemRequest(1L, "Name", user, Instant.now());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterId(anyLong())).thenReturn(List.of(itemRequest));

        Collection<ItemRequestDto> result = itemRequestService.getAllSelf(user.getId());
        Assertions.assertNotNull(result);
        verify(itemRequestRepository, times(1)).findByRequesterId(anyLong());
    }

    @Test
    void getAllSelfItemRequestsWhenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllSelf(userId);
        });

        assertEquals("Не найдено пользователя с id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(itemRequestRepository);
    }

    @Test
    void getItemRequestById() {
        ItemRequest itemRequest = new ItemRequest(1L, "Name", user, Instant.now());
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getById(itemRequest.getId());
        Assertions.assertNotNull(result);
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllOthersItemRequests() {
        int from = 0;
        int size = 10;
        ItemRequest itemRequest = new ItemRequest(1L, "Name", user, Instant.now());
        Item item = new Item(1L, "Item", "Description", true, user, null);

        when(itemRequestRepository.findAllByOrderByCreatedDesc(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        Page<ItemRequestDto> result = itemRequestService.getAllOthers(user.getId(), from, size);
        Assertions.assertNotNull(result);
        verify(itemRequestRepository, times(1)).findAllByOrderByCreatedDesc(any());
    }

    @Test
    void createItemRequest() {
        ItemRequest itemRequest = new ItemRequest(1L, "Name", user, Instant.now());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setRequesterId(user.getId());
        itemRequestDto.setDescription("Description");

        ItemRequestDto result = itemRequestService.create(user.getId(), itemRequestDto);
        Assertions.assertNotNull(result);
        verify(itemRequestRepository, times(1)).save(any());
    }
}
