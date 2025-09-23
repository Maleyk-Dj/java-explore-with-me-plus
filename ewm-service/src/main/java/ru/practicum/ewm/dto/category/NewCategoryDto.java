package ru.practicum.ewm.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.validation.FieldDescription;
import ru.practicum.validation.Marker;

import static ru.practicum.util.Constants.LENGTH_NAME_CATEGORY_MAX;
import static ru.practicum.util.Constants.LENGTH_NAME_CATEGORY_MIN;

@Data
@EqualsAndHashCode(of = {"name"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = LENGTH_NAME_CATEGORY_MIN, max = LENGTH_NAME_CATEGORY_MAX, groups = Marker.OnCreate.class)
    @FieldDescription("Наименование категории")
    String name;
}
