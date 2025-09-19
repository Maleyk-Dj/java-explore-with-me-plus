package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.PATTERN_FORMATE_DATE;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String annotation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    CategoryDto category;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // todo Long confirmedRequests; // количество одобренных заявок на участие в данном событии

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    //@JsonFormat(pattern = PATTERN_FORMATE_DATE)
    // todo LocalDateTime createdOn;    // дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = PATTERN_FORMATE_DATE)
    LocalDateTime eventDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // todo UserShortDto initiator;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocationDto location;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Boolean paid;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long participantLimit;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    //@JsonFormat(pattern = PATTERN_FORMATE_DATE)
    //todo LocalDateTime publishedOn; // дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Boolean requestModeration;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    //todo String state;    // список состояний жизненного цикла события

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String title;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    //todo Long views;  // количество просмотров
}
