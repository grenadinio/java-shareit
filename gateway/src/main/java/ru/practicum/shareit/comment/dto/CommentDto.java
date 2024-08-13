package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private Long itemId;
    private String authorName;
    @FutureOrPresent
    private LocalDateTime created;
}
