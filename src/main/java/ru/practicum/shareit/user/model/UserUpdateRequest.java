package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UserUpdateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
}
