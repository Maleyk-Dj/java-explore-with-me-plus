package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;

import static ru.practicum.util.Constants.OFFSET_EVENT_DATE;

public class EventDateValidator implements ConstraintValidator<EventDate, Instant> {
    @Override
    public boolean isValid(Instant value, ConstraintValidatorContext context) {
        if (value == null) return true; //для этого случая, считаю валидацию успешной

        return value.isAfter(Instant.now().plusSeconds(OFFSET_EVENT_DATE));
    }
}
