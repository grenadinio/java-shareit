package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Map;

public interface ItemService {
    ItemDto get(Long id);

    Collection<ItemDto> getAllByUser(Long userId);

    Collection<ItemDto> search(String text);

    ItemDto create(ItemDto item, Long userId);

    ItemDto edit(Long itemId, Long userId, Map<String, Object> updates);
}
