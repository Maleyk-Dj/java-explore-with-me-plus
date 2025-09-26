package ru.practicum.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventUserRequest;
import ru.practicum.events.service.EventService;
import ru.practicum.validation.Marker;

import java.util.Collection;

import static ru.practicum.util.Constants.*;

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
    EventFullDto add(@PathVariable(name = PATH_VARIABLE_USER_ID) @Positive(groups = Marker.OnCreate.class) Long userId,
                     @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен запрос: Добавить новое событие {}", newEventDto.getAnnotation());

        return eventService.add(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    @Validated(Marker.OnUpdate.class)
    @ResponseStatus(HttpStatus.OK)
    EventFullDto update(@PathVariable(name = PATH_VARIABLE_USER_ID) @Positive(groups = Marker.OnUpdate.class) Long userId,
                        @PathVariable(name = PATH_VARIABLE_EVENT_ID) @Positive(groups = Marker.OnUpdate.class) Long eventId,
                        @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Получен запрос: Обновить событие пользователем {}", updateEventUserRequest.getAnnotation());

        return eventService.update(userId, eventId, updateEventUserRequest);
    }

    @GetMapping
    public Collection<EventShortDto> findAll(@PathVariable(name = PATH_VARIABLE_USER_ID) @Positive Long userId,
                                             @RequestParam(name = REQUEST_PARAM_FROM, defaultValue = DEFAULT_VALUE_0) @PositiveOrZero int from,
                                             @RequestParam(name = REQUEST_PARAM_SIZE, defaultValue = DEFAULT_VALUE_REQUEST_PARAM_SIZE) @Positive int size) {
        log.info("Получен запрос: Получить список событий пользователя c id = {} в количестве size = {} с отступом from = {}", userId, size, from);

        return eventService.findAllByUser(userId, from, size);
    }
}
