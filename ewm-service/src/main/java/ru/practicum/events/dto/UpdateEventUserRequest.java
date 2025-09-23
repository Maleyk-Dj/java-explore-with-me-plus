package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.convertors.StringToEventStateConverter;
import ru.practicum.events.enums.EventState;
import ru.practicum.validation.EventDate;
import ru.practicum.validation.Marker;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = LENGTH_ANNOTATION_EVENT_MIN, max = LENGTH_ANNOTATION_EVENT_MAX, message = "Длина краткого описания события не прошла валидацию.", groups = Marker.OnUpdate.class)
    String annotation;

    @JsonProperty(value = "category", access = JsonProperty.Access.WRITE_ONLY)
    Long categoryId;  // категория к которой относится событие

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = LENGTH_DESCRIPTION_EVENT_MIN, max = LENGTH_DESCRIPTION_EVENT_MAX, message = "Длина описания события не прошла валидацию.", groups = Marker.OnUpdate.class)
    String description;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(pattern = PATTERN_FORMATE_DATE)
    @EventDate(groups = Marker.OnUpdate.class)
    LocalDateTime eventDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    LocationDto location;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Boolean paid;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long participantLimit;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Boolean requestModeration;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(converter = StringToEventStateConverter.class)
    EventState stateAction;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = LENGTH_TITLE_EVENT_MIN, max = LENGTH_TITLE_EVENT_MAX, message = "Длина заголовка события не прошла валидацию.", groups = Marker.OnUpdate.class)
    String title;
}
