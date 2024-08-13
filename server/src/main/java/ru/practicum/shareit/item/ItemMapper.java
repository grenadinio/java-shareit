package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest
        );
    }

    public ItemBookingDto toItemBookingDto(Item item, BookingDto lastBooking, BookingDto nextBooking, Long userId, List<CommentDto> comments) {
        return new ItemBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest(),
                item.getOwner().getId().equals(userId) ? lastBooking : null,
                item.getOwner().getId().equals(userId) ? nextBooking : null,
                comments
        );
    }

    public ItemRequestingDto toItemRequestingDto(Item item) {
        return new ItemRequestingDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
