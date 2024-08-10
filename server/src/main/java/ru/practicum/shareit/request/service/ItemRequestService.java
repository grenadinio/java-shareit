package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@Transactional(readOnly = true)
public interface ItemRequestService {
    Collection<ItemRequestDto> getAllSelf(Long userId);

    ItemRequestDto getById(Long id);

    Page<ItemRequestDto> getAllOthers(Long userId, Integer from, Integer size);

    @Transactional
    ItemRequestDto create(Long userId, ItemRequestDto itemRequest);
}
