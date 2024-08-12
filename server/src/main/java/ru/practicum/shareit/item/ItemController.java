package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemBookingDto get(@PathVariable Long id, @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.get(id, userId);
    }

    @GetMapping
    public Collection<ItemBookingDto> getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getAllByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto item, @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.create(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto comment(@PathVariable Long itemId,
                              @RequestHeader(USER_ID_HEADER) Long userId, @RequestBody CommentDto comment) {
        return itemService.comment(itemId, userId, comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(@PathVariable Long itemId,
                        @RequestHeader(USER_ID_HEADER) Long userId, @RequestBody Map<String, Object> updates) {
        return itemService.edit(itemId, userId, updates);
    }
}
