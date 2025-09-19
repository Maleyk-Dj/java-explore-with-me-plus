package ru.practicum.events.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
class ApiError {
    private String reason;
    private String message;
    private HttpStatus status;
    private List<String> errors;
    private LocalDateTime timestamp;

    public ApiError(String reason, String message, HttpStatus status) {
        this(reason, message, status, new ArrayList<>(), LocalDateTime.now());
    }

    public ApiError(String reason, String message, HttpStatus status, List<String> errors) {
        this(reason, message, status, errors, LocalDateTime.now());
    }
}
