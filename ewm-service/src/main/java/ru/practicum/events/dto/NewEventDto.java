package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.validation.EventDate;
import ru.practicum.validation.Marker;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = LENGTH_ANNOTATION_EVENT_MIN, max = LENGTH_ANNOTATION_EVENT_MAX, message = "Длина краткого описания события не прошла валидацию.")
    @NotBlank(message = "Краткое описание события не может быть пустым.", groups = Marker.OnCreate.class)
    String annotation;

    @JsonProperty(value = "category", access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Категория должна быть указана при создании события.", groups = Marker.OnCreate.class)
    Long categoryId;  // категория к которой относится событие

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = LENGTH_DESCRIPTION_EVENT_MIN, max = LENGTH_DESCRIPTION_EVENT_MAX, message = "Длина описания события не прошла валидацию.")
    @NotBlank(message = "Описание события не может быть пустым.", groups = Marker.OnCreate.class)
    String description;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(pattern = PATTERN_FORMATE_DATE)
    @EventDate
    LocalDateTime eventDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Нужно указать координаты места события.", groups = Marker.OnCreate.class)
    LocationDto location;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Boolean paid;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long participantLimit;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Boolean requestModeration;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = LENGTH_TITLE_EVENT_MIN, max = LENGTH_TITLE_EVENT_MAX, message = "Длина заголовка события не прошла валидацию.")
    String title;
}
