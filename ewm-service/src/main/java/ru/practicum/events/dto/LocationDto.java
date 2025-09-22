package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.validation.Marker;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Нужно указать широту места события.", groups = Marker.OnCreate.class)
    Float lat;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Нужно указать долготу места события.", groups = Marker.OnCreate.class)
    Float lon;
}
