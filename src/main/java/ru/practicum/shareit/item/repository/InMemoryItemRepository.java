package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private final AtomicLong id = new AtomicLong(0);

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getAllByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }


    @Override
    public Collection<Item> search(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item edit(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    private Long generateId() {
        return id.incrementAndGet();
    }
}
