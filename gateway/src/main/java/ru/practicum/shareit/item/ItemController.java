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
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public Object get(@PathVariable Long id,
                      @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public Object getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getAllByUser(userId);
    }

    @GetMapping("/search")
    public Object search(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @RequestParam String text) {
        return itemClient.search(userId, text);
    }

    @PostMapping
    public Object create(@Valid @RequestBody ItemDto item,
                         @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.create(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public Object comment(@PathVariable Long itemId,
                          @RequestHeader(USER_ID_HEADER) Long userId,
                                          @RequestBody @Valid CommentDto comment) {
        return itemClient.comment(itemId, userId, comment);
    }

    @PatchMapping("/{itemId}")
    public Object edit(@PathVariable Long itemId,
                       @RequestHeader(USER_ID_HEADER) Long userId,
                                       @RequestBody Map<String, Object> updates) {
        return itemClient.edit(itemId, userId, updates);
    }
}
