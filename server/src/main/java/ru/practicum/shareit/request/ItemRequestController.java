package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping
    public Collection<ItemRequestDto> getAllSelf(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllSelf(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(@PathVariable Long id) {
        return itemRequestService.getById(id);
    }

    @GetMapping("/all")
    public Page<ItemRequestDto> getAllOthers(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestParam Integer from,
                                             @RequestParam Integer size) {
        return itemRequestService.getAllOthers(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID_HEADER) Long userId, @RequestBody ItemRequestDto itemRequest) {
        return itemRequestService.create(userId, itemRequest);
    }
}
