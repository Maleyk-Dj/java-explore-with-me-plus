package ru.practicum.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventUserRequest;
import ru.practicum.events.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.validation.Marker;

import static ru.practicum.util.Constants.PATH_VARIABLE_EVENT_ID;
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
}
