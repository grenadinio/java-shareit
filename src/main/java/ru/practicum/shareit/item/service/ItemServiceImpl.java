package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto get(Long id) {
        return itemMapper.toItemDto(findItemById(id));
    }

    public Collection<ItemDto> getAllByUser(Long userId) {
        return itemRepository.getAllByUser(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) return Collections.emptyList();
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = findUserById(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.create(item));
    }

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

        return itemMapper.toItemDto(itemRepository.edit(item));
    }

    private User findUserById(Long userId) {
        return userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Не найдено пользователя с id: " + userId));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдено предмета с id: " + itemId));
    }
}
