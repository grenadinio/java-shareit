package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public Object getAllSelf(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getAllSelf(userId);
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable Long id,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getById(id, userId);
    }

    @GetMapping("/all")
    public Object getAllOthers(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return itemRequestClient.getAllOthers(userId, from, size);
    }

    @PostMapping
    public Object create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequest) {
        return itemRequestClient.create(userId, itemRequest);
    }
}
