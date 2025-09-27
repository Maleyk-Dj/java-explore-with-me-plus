package ru.practicum.events.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.Location;
import ru.practicum.events.model.Event;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventFullDto toEventFullDto(Event event) {
        if (event == null) {
            return null;
        }

        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(event.getCategory().getId());
        categoryDto.setName(event.getCategory().getName());
        dto.setCategory(categoryDto);

        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0);
        dto.setCreatedOn(event.getCreatedOn() != null ?
                event.getCreatedOn().format(formatter) : null);
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate().format(formatter)); // Здесь форматирование работает

        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(event.getInitiator().getId());
        userShortDto.setName(event.getInitiator().getName());
        dto.setInitiator(userShortDto);

        Location location = new Location();
        location.setLat(event.getLocationLat());
        location.setLon(event.getLocationLon());
        dto.setLocation(location);

        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn() != null ?
                event.getPublishedOn().format(formatter) : null);
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState().name());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);

        return dto;
    }

    public EventShortDto toEventShortDto(Event event) {
        if (event == null) {
            return null;
        }

        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(event.getCategory().getId());
        categoryDto.setName(event.getCategory().getName());
        dto.setCategory(categoryDto);

        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0);
        dto.setEventDate(event.getEventDate().format(formatter));
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(event.getInitiator().getId());
        userShortDto.setName(event.getInitiator().getName());
        dto.setInitiator(userShortDto);

        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);

        return dto;
    }
}
