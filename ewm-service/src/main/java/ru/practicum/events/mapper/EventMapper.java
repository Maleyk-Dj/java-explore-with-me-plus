package ru.practicum.events.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.Location;
import ru.practicum.events.model.Event;

@Component
public class EventMapper {

    public EventFullDto toEventFullDto(Event event) {
        if (event == null) {
            return null;
        }

        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());

        // Создаем CategoryDto через сеттеры
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(event.getCategory().getId());
        categoryDto.setName(event.getCategory().getName());
        dto.setCategory(categoryDto);

        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setCreatedOn(String.valueOf(event.getCreatedOn()));
        dto.setDescription(event.getDescription());
        dto.setEventDate(String.valueOf(event.getEventDate()));

        // Создаем UserShortDto через сеттеры
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(event.getInitiator().getId());
        userShortDto.setName(event.getInitiator().getName());
        dto.setInitiator(userShortDto);

        // Создаем Location через сеттеры
        Location location = new Location();
        location.setLat(event.getLocationLat());
        location.setLon(event.getLocationLon());
        dto.setLocation(location);

        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(String.valueOf(event.getPublishedOn()));
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState().name());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());

        return dto;
    }

    public EventShortDto toEventShortDto(Event event) {
        if (event == null) {
            return null;
        }

        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());

        // Создаем CategoryDto через сеттеры
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(event.getCategory().getId());
        categoryDto.setName(event.getCategory().getName());
        dto.setCategory(categoryDto);

        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setEventDate(event.getEventDate());

        // Создаем UserShortDto через сеттеры
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(event.getInitiator().getId());
        userShortDto.setName(event.getInitiator().getName());
        dto.setInitiator(userShortDto);

        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());

        return dto;
    }
}
