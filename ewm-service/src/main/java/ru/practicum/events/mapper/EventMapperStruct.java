package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventUserRequest;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.model.Event;
import ru.practicum.ewm.user.model.User;
import ru.practicum.mapper.IgnoreUnmappedMapperConfig;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, config = IgnoreUnmappedMapperConfig.class)
public interface EventMapperStruct {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "newEventDto.location.lat", target = "locationLat")
    @Mapping(source = "newEventDto.location.lon", target = "locationLon")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "user", target = "initiator")
    @Mapping(source = "newEventDto", target = "state", qualifiedByName = "getState")
    @Mapping(source = "newEventDto", target = "createdOn", qualifiedByName = "getCreatedOn")
    Event toEvent(User user, NewEventDto newEventDto, Category category);

    @Named("getState")
    default EventState getState(NewEventDto newEventDto) {
        return EventState.PENDING;
    }

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
}
