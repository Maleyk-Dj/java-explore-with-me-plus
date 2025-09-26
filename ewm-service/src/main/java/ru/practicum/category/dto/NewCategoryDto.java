package ru.practicum.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    String name;
}
