package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> get(Long id);

    Collection<Item> getAllByUser(Long userId);

    Collection<Item> search(String text);

    Item create(Item item);

    Item edit(Item item);
}
