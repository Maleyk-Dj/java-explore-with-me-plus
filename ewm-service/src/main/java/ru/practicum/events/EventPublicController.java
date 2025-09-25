package ru.practicum.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.params.PublicEventParams;
import ru.practicum.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest request) {
        return eventService.getPublicEventById(eventId, request);
    }

    @GetMapping
    public List<EventShortDto> searchEvents(@ModelAttribute PublicEventParams params, HttpServletRequest request) {
        return eventService.searchPublicEvents(params, request);
    }
}

