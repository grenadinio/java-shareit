package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public Collection<ItemRequestDto> getAllSelf(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найдено пользователя с id: " + userId));
        return itemRequestRepository.findByRequesterId(userId).stream()
                .map(itemRequest -> {
                    List<ItemRequestingDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                            .map(itemMapper::toItemRequestingDto)
                            .toList();
                    return itemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено запроса предмета с id: " + id));
        List<ItemRequestingDto> items = itemRepository.findAllByRequestId(id).stream()
                .map(itemMapper::toItemRequestingDto)
                .toList();
        return itemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public Page<ItemRequestDto> getAllOthers(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestRepository.findAllByOrderByCreatedDesc(pageable).map(itemRequest -> {
            List<ItemRequestingDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                    .map(itemMapper::toItemRequestingDto)
                    .toList();
            return itemRequestMapper.toItemRequestDto(itemRequest, items);
        });
    }

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto request) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найдено пользователя с id: " + userId));
        ItemRequest itemRequest = new ItemRequest(0L, request.getDescription(), requester, Instant.now());

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }
}
