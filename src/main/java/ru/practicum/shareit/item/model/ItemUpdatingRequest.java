package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ItemUpdatingRequest {

    private long userId;
    private long itemId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @BooleanFlag
    private Boolean available;
}
