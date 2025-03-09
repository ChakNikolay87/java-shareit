package ru.practicum.shareit.exception;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private String error;
}