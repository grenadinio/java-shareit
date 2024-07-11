package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id) {
        return itemService.get(id);
    }

    @GetMapping
    public Collection<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody Map<String, Object> updates) {
        return itemService.edit(itemId, userId, updates);
    }
}
