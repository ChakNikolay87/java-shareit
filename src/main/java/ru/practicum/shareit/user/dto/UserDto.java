package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private Long id;
    @NotBlank(message = "Имя не может быть пустым")

    private String name;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат Email")
    private String email;
}