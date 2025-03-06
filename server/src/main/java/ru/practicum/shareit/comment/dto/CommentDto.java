package ru.practicum.shareit.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommentDto {
    private Long id;
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}