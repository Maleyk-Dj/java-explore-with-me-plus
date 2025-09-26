package ru.practicum.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.validation.Marker;

import java.util.Collection;
import java.util.List;

import static ru.practicum.util.Constants.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
public class EventsPrivateController {
    private final EventService eventService;

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
    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto rejectRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId,
                                                 @RequestParam @Positive Long requestId) {
        return eventService.rejectRequest(userId, eventId, requestId);
    }

    @GetMapping
    public Collection<EventShortDto> findAll(@PathVariable(name = PATH_VARIABLE_USER_ID) @Positive Long userId,
                                             @RequestParam(name = REQUEST_PARAM_FROM, defaultValue = DEFAULT_VALUE_0) @PositiveOrZero int from,
                                             @RequestParam(name = REQUEST_PARAM_SIZE, defaultValue = DEFAULT_VALUE_REQUEST_PARAM_SIZE) @Positive int size) {
        log.info("Получен запрос: Получить список событий пользователя c id = {} в количестве size = {} с отступом from = {}", userId, size, from);

        return eventService.findAllByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    EventFullDto findById(@PathVariable(name = PATH_VARIABLE_USER_ID) @Positive Long userId,
                          @PathVariable(name = PATH_VARIABLE_EVENT_ID) @Positive Long eventId) {
        log.info("Получен запрос: Получить данные по событию c id = {} у пользователя с id = {}.", eventId, userId);

        return eventService.findByUserAndEvent(userId, eventId);
    }
    // GET /users/{userId}/events/{eventId}/requests
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {

        // В сервисе должна быть проверка, что userId является инициатором eventId
        return eventService.getRequestsByEvent(userId, eventId);
    }
}
