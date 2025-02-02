package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class ItemDto {

    private long id;
    @NotBlank

    private String name;
    @NotBlank

    private String description;

    @NotNull
    @Getter
    private Boolean available;
}
