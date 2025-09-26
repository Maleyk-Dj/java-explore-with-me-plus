package ru.practicum.events.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.events.dto.*;
import ru.practicum.events.params.AdminEventParams;
import ru.practicum.events.params.PublicEventParams;

import java.util.List;

public interface EventService {
    List<EventFullDto> search(AdminEventParams params);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    public EventFullDto add(Long userId, NewEventDto newEventDto);

    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> searchPublicEvents(PublicEventParams params, HttpServletRequest request);

    List<EventShortDto> findAllByUser(Long userId, int from, int size);
}
