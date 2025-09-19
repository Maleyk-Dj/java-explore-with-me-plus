package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.mapper.IgnoreUnmappedMapperConfig;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, config = IgnoreUnmappedMapperConfig.class)
public interface EventMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "newEventDto.location.lat", target = "lat")
    @Mapping(source = "newEventDto.location.lon", target = "lon")
    @Mapping(source = "newEventDto.eventDate", target = "eventDate", qualifiedByName = "toInstant")
    @Mapping(source = "category", target = "category")
    Event toEvent(Long userId, NewEventDto newEventDto, Category category);

    @Named("toInstant")
    default Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    EventFullDto toEventFullDto(Event event);
}
