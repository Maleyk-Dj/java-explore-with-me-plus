package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.category.model.Category;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventUserRequest;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.enums.EventStateAction;
import ru.practicum.events.model.Event;
import ru.practicum.ewm.user.model.User;
import ru.practicum.mapper.IgnoreUnmappedMapperConfig;

import java.time.LocalDateTime;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        config = IgnoreUnmappedMapperConfig.class
)
public interface EventMapperStruct {

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "newEventDto.location.lat", target = "locationLat")
    @Mapping(source = "newEventDto.location.lon", target = "locationLon")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "user", target = "initiator")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(source = "newEventDto", target = "createdOn", qualifiedByName = "getCreatedOn")
    @Mapping(source = "newEventDto.paid", target = "paid", defaultValue = "false")
    @Mapping(source = "newEventDto.participantLimit", target = "participantLimit", defaultValue = "0")
    @Mapping(source = "newEventDto.requestModeration", target = "requestModeration", defaultValue = "true")
    Event toEvent(User user, NewEventDto newEventDto, Category category);

    @Named("getCreatedOn")
    default LocalDateTime getCreatedOn(NewEventDto newEventDto) {
        return LocalDateTime.now();
    }

    @Mapping(source = "eventId", target = "id")
    @Mapping(source = "updateEventUserRequest.location.lat", target = "locationLat")
    @Mapping(source = "updateEventUserRequest.location.lon", target = "locationLon")
    @Mapping(source = "user", target = "initiator")
    @Mapping(source = "updateEventUserRequest.stateAction", target = "state")
    Event toEvent(User user, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    default EventState map(EventStateAction stateAction) {
        if (stateAction == null) return null;
        return switch (stateAction) {
            case PUBLISH_EVENT -> EventState.PUBLISHED;
            case REJECT_EVENT -> EventState.CANCELED;
        };
    }
}