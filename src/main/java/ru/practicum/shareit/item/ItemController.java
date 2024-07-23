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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemBookingDto get(@PathVariable Long id, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.get(id, userId);
    }

    @GetMapping
    public Collection<ItemBookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
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

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody Comment comment) {
        return itemService.comment(itemId, userId, comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable Long itemId,
                        @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody Map<String, Object> updates) {
        return itemService.edit(itemId, userId, updates);
    }
}
