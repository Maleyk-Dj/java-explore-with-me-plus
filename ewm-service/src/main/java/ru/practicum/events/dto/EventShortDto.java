package ru.practicum.events.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {
    private Long id;

    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000, message = "Аннотация должна содержать от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "Категория не может быть пустой")
    private CategoryDto category;

    private Integer confirmedRequests;

    @NotNull(message = "Дата события не может быть пустой")
    @Future(message = "Дата события должна не должна быть раньше даты начала")
    private LocalDateTime eventDate;

    @NotNull(message = "Инициатор не может быть равен null")
    private UserShortDto initiator;

    @NotNull(message = "Paid cannot be null")
    private Boolean paid;

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок должен содержать от 3 до 120 символов")
    private String title;

    private Long views;
}
