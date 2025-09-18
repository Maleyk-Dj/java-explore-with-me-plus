package ru.practicum.error.handling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final List<Violation> violations;   // список нарушений
    private final Boolean error = true; // в тестах спринта 16 стало необходимым поле с таким названием
}
