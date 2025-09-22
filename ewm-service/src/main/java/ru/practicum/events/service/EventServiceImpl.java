package ru.practicum.events.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.enums.EventStateAction;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.mapper.EventMapperStruct;
import ru.practicum.events.model.Event;
import ru.practicum.events.params.AdminEventParams;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
//@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventMapperStruct eventMapperStruct;
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventFullDto> search(AdminEventParams params) {
        validatePaginationParams(params);
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Specification<Event> spec = buildAdminSpecification(params);
        Page<Event> events = eventRepository.findAll(spec, pageable);

        return events.getContent().stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }

        updateEventFields(event, dto);
        handleStateAction(event, dto.getStateAction());

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    private Specification<Event> buildAdminSpecification(AdminEventParams params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по пользователям
            if (params.getUsers() != null && !params.getUsers().isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(params.getUsers()));
            }

            // Фильтр по состояниям
            if (params.getStates() != null && !params.getStates().isEmpty()) {
                List<EventState> eventStates = params.getStates().stream()
                        .map(state -> {
                            try {
                                return EventState.valueOf(state.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                throw new ValidationException("Invalid state: " + state);
                            }
                        })
                        .collect(Collectors.toList());
                predicates.add(root.get("state").in(eventStates));
            }

            // Фильтр по категориям
            if (params.getCategories() != null && !params.getCategories().isEmpty()) {
                predicates.add(root.get("category").get("id").in(params.getCategories()));
            }

            // Фильтр по дате начала
            if (params.getRangeStart() != null) {
                LocalDateTime start = parseDateTime(params.getRangeStart());
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), start));
            }

            // Фильтр по дате окончания
            if (params.getRangeEnd() != null) {
                LocalDateTime end = parseDateTime(params.getRangeEnd());
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), end));
            }

            // Валидация диапазона дат
            if (params.getRangeStart() != null && params.getRangeEnd() != null) {
                LocalDateTime start = parseDateTime(params.getRangeStart());
                LocalDateTime end = parseDateTime(params.getRangeEnd());
                if (end.isBefore(start)) {
                    throw new ValidationException("RangeEnd cannot be before rangeStart");
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void updateEventFields(Event event, UpdateEventAdminRequest dto) {
        if (dto.getAnnotation() != null) {
            validateAnnotation(dto.getAnnotation());
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null) {
            validateDescription(dto.getDescription());
            event.setDescription(dto.getDescription());
        }

        if (dto.getTitle() != null) {
            validateTitle(dto.getTitle());
            event.setTitle(dto.getTitle());
        }

        if (dto.getEventDate() != null) {
            validateEventDate(dto.getEventDate());
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            validateParticipantLimit(dto.getParticipantLimit());
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
    }

    private void handleStateAction(Event event, EventStateAction stateAction) {
        if (stateAction == null) return;

        switch (stateAction) {
            case PUBLISH_EVENT:
                validatePublishEvent(event);
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;

            case REJECT_EVENT:
                validateRejectEvent(event);
                event.setState(EventState.CANCELED);
                break;
        }
    }

    private void validatePublishEvent(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Cannot publish event that is not in PENDING state");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Cannot publish event less than 1 hour before event date");
        }
    }

    private void validateRejectEvent(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot reject already published event");
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Expected: yyyy-MM-dd HH:mm:ss");
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Event date cannot be in the past");
        }
    }

    private void validatePaginationParams(AdminEventParams params) {
        if (params.getFrom() < 0) {
            throw new ValidationException("From must be >= 0");
        }
        if (params.getSize() <= 0) {
            throw new ValidationException("Size must be > 0");
        }
        if (params.getSize() > 1000) {
            throw new ValidationException("Size cannot exceed 1000");
        }
    }

    private void validateAnnotation(String annotation) {
        if (annotation.length() < 20 || annotation.length() > 2000) {
            throw new ValidationException("Annotation must be between 20 and 2000 characters");
        }
        if (annotation.trim().isEmpty()) {
            throw new ValidationException("Annotation cannot be empty or contain only spaces");
        }
    }

    private void validateDescription(String description) {
        if (description.length() < 20 || description.length() > 7000) {
            throw new ValidationException("Description must be between 20 and 7000 characters");
        }
        if (description.trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty or contain only spaces");
        }
    }

    private void validateTitle(String title) {
        if (title.length() < 3 || title.length() > 120) {
            throw new ValidationException("Title must be between 3 and 120 characters");
        }
        if (title.trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty or contain only spaces");
        }
    }

    private void validateParticipantLimit(Integer participantLimit) {
        if (participantLimit < 0) {
            throw new ValidationException("Participant limit cannot be negative");
        }
    }

    @Override
    public EventFullDto add(Long userId, NewEventDto newEventDto) {
        Category category = categoryRepository.findById(newEventDto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Категория с id = " + newEventDto.getCategoryId() + " не найдена.", log));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        Event event = eventMapperStruct.toEvent(user, newEventDto, category);

        event = eventRepository.save(event);

        log.info("Добавлено новое событие {}.", event);

        return eventMapper.toEventFullDto(event);
    }
}
