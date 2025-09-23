package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.EventAdminFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.Location;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventAdminFullDto toEventFullDto(Event event) {
        if (event == null) {
            return null;
        }

        EventAdminFullDto dto = new EventAdminFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(event.getCategory().getId());
        categoryDto.setName(event.getCategory().getName());
        dto.setCategory(categoryDto);

        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setCreatedOn(event.getCreatedOn() != null ?
                event.getCreatedOn().format(formatter) : null);
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate().format(formatter));

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

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(event.getCategory().getId());
        categoryDto.setName(event.getCategory().getName());
        dto.setCategory(categoryDto);

        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setEventDate(event.getEventDate());

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
