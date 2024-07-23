package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Map;

@Transactional(readOnly = true)
public interface ItemService {
    ItemBookingDto get(Long id, Long userId);

    Collection<ItemBookingDto> getAllByUser(Long userId);

    Collection<ItemDto> search(String text);

    @Transactional
    ItemDto create(ItemDto item, Long userId);

    @Transactional
    ItemDto edit(Long itemId, Long userId, Map<String, Object> updates);

    CommentDto comment(Long itemId, Long userId, Comment comment);
}
