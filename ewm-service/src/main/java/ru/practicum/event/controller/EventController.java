package ru.practicum.event.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;

import static ru.practicum.util.Constants.PATH_VARIABLE_USER_ID;

@Validated
@RestController
@RequestMapping("/users/{userId}/events")
public class EventController {
    private final EventService eventService;

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    public EventController(@Qualifier("EventServiceImpl") EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    EventFullDto add(@PathVariable(name = PATH_VARIABLE_USER_ID) Long userId,
                     @RequestBody NewEventDto newEventDto) {
        log.info("Получен запрос: Добавить новое событие {}", newEventDto.getAnnotation());

        return eventService.add(userId, newEventDto);
    }
}
