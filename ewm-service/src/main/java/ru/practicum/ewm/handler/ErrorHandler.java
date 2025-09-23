package ru.practicum.ewm.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.handler.exception.ConflictException;
import ru.practicum.ewm.handler.exception.NotFoundException;
import ru.practicum.ewm.handler.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ApiError api(HttpStatus status, String reason, String message, List<String> errors) {
        return new ApiError(
                errors == null ? List.of() : errors,
                message,
                reason,
                status.name(),
                LocalDateTime.now().format(FMT)
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return api(HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage(),
                null);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        log.warn("409 {}", e.getMessage());
        return api(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getMessage(),
                null);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException ex) {
        log.warn("400 {}", ex.getMessage());
        return api(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage(),
                null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> "Field: %s. Error: %s. Value: %s"
                        .formatted(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .orElse("Validation failed");
        return api(HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                message,
                null);
    }
}