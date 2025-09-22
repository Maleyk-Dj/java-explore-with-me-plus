package ru.practicum.events;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.service.EventService;
import ru.practicum.validation.Marker;

import static ru.practicum.util.Constants.PATH_VARIABLE_USER_ID;

@Validated
@RestController
@RequestMapping("/users/{userId}/events")
public class EventsPrivateController {
    private final EventService eventService;

    private static final Logger log = LoggerFactory.getLogger(EventsPrivateController.class);

    @Autowired
    public EventsPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto add(@PathVariable(name = PATH_VARIABLE_USER_ID) Long userId,
                     @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен запрос: Добавить новое событие {}", newEventDto.getAnnotation());

        return eventService.add(userId, newEventDto);
    }
}
