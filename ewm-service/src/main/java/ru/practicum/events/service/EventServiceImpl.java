package ru.practicum.events.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.client.StatClient;
import ru.practicum.events.dto.*;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.enums.EventStateAction;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.mapper.EventMapperStruct;
import ru.practicum.events.model.Event;
import ru.practicum.events.params.AdminEventParams;
import ru.practicum.events.params.PublicEventParams;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.handler.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;
import ru.practicum.util.Reflection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
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
    private final StatClient statClient;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime EPOCH = LocalDateTime.of(1970, 1, 1, 0, 0);


    @Override
    public List<EventFullDto> search(AdminEventParams params) {
        validatePaginationParams(params);
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Specification<Event> spec = buildAdminSpecification(params);
        Page<Event> events = eventRepository.findAll(spec, pageable);

        // Получаем ID всех найденных событий
        List<Long> eventIds = events.getContent().stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // Получаем подтвержденные запросы для всех событий
        Map<Long, Integer> confirmedRequestsMap = getConfirmedRequestsForEvents(eventIds);

        // Получаем просмотры для всех событий (с обработкой ошибок)
        Map<Long, Long> viewsMap = getViewsForEvents(eventIds);

        return events.getContent().stream()
                .map(event -> {
                    EventFullDto dto = eventMapper.toEventFullDto(event);
                    dto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0));
                    dto.setViews(viewsMap.getOrDefault(event.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> getConfirmedRequestsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Integer> confirmedMap = new HashMap<>();

        for (Long eventId : eventIds) {
            try {
                Integer confirmed = requestRepository.countConfirmedByEventId(eventId);
                confirmedMap.put(eventId, confirmed != null ? confirmed : 0);
            } catch (Exception e) {
                log.warn("Failed to get confirmed requests for event {}: {}", eventId, e.getMessage());
                confirmedMap.put(eventId, 0);
            }
        }

        return confirmedMap;
    }

    private Map<Long, Long> getViewsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Long> viewsMap = new HashMap<>();

        // Если сервис статистики недоступен, возвращаем 0 для всех событий
        for (Long eventId : eventIds) {
            viewsMap.put(eventId, 0L);
        }

        // Попытка получить статистику, если сервис доступен
        try {
            List<String> uris = eventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());

            LocalDateTime start = EPOCH;
            LocalDateTime end = LocalDateTime.now();

            List<ViewStatsDto> stats = statClient.getStats(start, end, uris, true);

            // Обновляем карту просмотров
            for (ViewStatsDto stat : stats) {
                try {
                    Long eventId = extractEventIdFromUri(stat.getUri());
                    if (eventId != null) {
                        viewsMap.put(eventId, stat.getHits());
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse event ID from URI: {}", stat.getUri());
                }
            }
        } catch (Exception ex) {
            log.warn("Stat service unavailable, using default views (0)");
            // Оставляем значения по умолчанию (0)
        }

        return viewsMap;
    }

    private Long extractEventIdFromUri(String uri) {
        if (uri == null || !uri.startsWith("/events/")) {
            return null;
        }
        try {
            return Long.parseLong(uri.substring("/events/".length()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private long getEventViews(Long eventId) {
        try {
            LocalDateTime start = EPOCH;
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = List.of("/events/" + eventId);

            List<ViewStatsDto> stats = statClient.getStats(start, end, uris, true);
            return stats.stream().mapToLong(ViewStatsDto::getHits).sum();
        } catch (Exception ex) {
            log.warn("Failed to fetch views for event {}: {}", eventId, ex.getMessage());
            return 0L;
        }
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

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (!"PUBLISHED".equalsIgnoreCase(event.getState().name())) {
            throw new NotFoundException("Event with id=" + eventId + " is not published");
        } //записываем хит
        try {
            EndpointHitDto hit = EndpointHitDto.builder()
                    .app("ewm-main-service")
                    .uri("/events/" + eventId)
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build();
            statClient.hit(hit);
        } catch (Exception ex) {
            log.warn("Stat hit failed for event {}: {}", eventId, ex.getMessage());
        }

        // получить просмотры
        LocalDateTime start = event.getPublishedOn() != null ? event.getPublishedOn()
                : (event.getCreatedOn() != null ? event.getCreatedOn() : EPOCH);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/" + eventId);
        long views = 0L;
        try {
            List<ViewStatsDto> stats = statClient.getStats(start, end, uris, true);
            views = stats.stream().mapToLong(ViewStatsDto::getHits).sum();
        } catch (Exception ex) {
            log.warn("Failed to fetch views for event {}: {}", eventId, ex.getMessage());
        }

        Integer confirmed = requestRepository.countConfirmedByEventId(eventId);
        confirmed = confirmed == null ? 0 : confirmed;

        EventFullDto dto = eventMapper.toEventFullDto(event);

        dto.setConfirmedRequests(confirmed);
        dto.setViews(views);

        return dto;
    }

    @Override
    public List<EventShortDto> searchPublicEvents(PublicEventParams params, HttpServletRequest request) {

        if (params.getFrom() != null && params.getFrom() < 0) throw new ValidationException("from must be >= 0");
        if (params.getSize() != null && (params.getSize() <= 0 || params.getSize() > 1000))
            throw new ValidationException("size must be between 1 and 1000");

        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

            if (params.getText() != null && !params.getText().isBlank()) {
                String pat = "%" + params.getText().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("annotation")), pat),
                        cb.like(cb.lower(root.get("description")), pat)
                ));
            }
            if (params.getCategories() != null && !params.getCategories().isEmpty()) {
                predicates.add(root.get("category").get("id").in(params.getCategories()));
            }
            if (params.getPaid() != null) {
                predicates.add(cb.equal(root.get("paid"), params.getPaid()));
            }
            LocalDateTime now = LocalDateTime.now();
            if ((params.getRangeStart() == null || params.getRangeStart().isBlank()) &&
                    (params.getRangeEnd() == null || params.getRangeEnd().isBlank())) {
                predicates.add(cb.greaterThan(root.get("eventDate"), now));
            } else {
                if (params.getRangeStart() != null && !params.getRangeStart().isBlank()) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"),
                            LocalDateTime.parse(params.getRangeStart(), formatter)));
                }
                if (params.getRangeEnd() != null && !params.getRangeEnd().isBlank()) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"),
                            LocalDateTime.parse(params.getRangeEnd(), formatter)));
                }
            }
            if (Boolean.TRUE.equals(params.getOnlyAvailable())) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Request> rq = sub.from(Request.class);
                sub.select(cb.count(rq));
                sub.where(cb.equal(rq.get("event").get("id"), root.get("id")),
                        cb.equal(rq.get("status"), "CONFIRMED"));
                predicates.add(cb.or(
                        cb.equal(root.get("participantLimit"), 0),
                        cb.greaterThan(root.get("participantLimit"), sub)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        int from = params.getFrom() == null ? 0 : params.getFrom();
        int size = params.getSize() == null ? 10 : params.getSize();
        Pageable pageable;
        if ("VIEWS".equalsIgnoreCase(params.getSort())) {
            pageable = PageRequest.of(from / size, size); // сортировать по views в памяти ниже
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        }

        Page<Event> page = eventRepository.findAll(spec, pageable);
        List<Event> events = page.getContent();

        try {
            EndpointHitDto hit = EndpointHitDto.builder()
                    .app("ewm-main-service")
                    .uri(request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""))
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build();
            statClient.hit(hit);
        } catch (Exception ex) {
            log.warn("Stat hit failed for search: {}", ex.getMessage());
        }
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toList());
        Map<Long, Long> viewsMap = new HashMap<>();
        if (!uris.isEmpty()) {
            try {
                LocalDateTime start = (params.getRangeStart() != null && !params.getRangeStart().isBlank())
                        ? LocalDateTime.parse(params.getRangeStart(), formatter) : EPOCH;
                LocalDateTime end = (params.getRangeEnd() != null && !params.getRangeEnd().isBlank())
                        ? LocalDateTime.parse(params.getRangeEnd(), formatter) : LocalDateTime.now();
                List<ViewStatsDto> stats = statClient.getStats(start, end, uris, false);
                for (ViewStatsDto s : stats) {
                    String uri = s.getUri();
                    if (uri == null) continue;
                    Long id = Long.valueOf(uri.substring(uri.lastIndexOf('/') + 1));
                    viewsMap.put(id, s.getHits());
                }
            } catch (Exception ex) {
                log.warn("Failed to fetch batch views: {}", ex.getMessage());
            }
        }
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Integer> confirmedMap = new HashMap<>();
        if (!ids.isEmpty()) {
            List<ConfirmedCount> counts = requestRepository.countConfirmedForEventIds(ids);
            if (counts != null) {
                for (ConfirmedCount c : counts) {
                    confirmedMap.put(c.getEventId(), c.getCnt());
                }
            }
        }
        List<EventShortDto> dtos = events.stream().map(e -> {
            EventShortDto s = eventMapper.toEventShortDto(e);
            s.setViews(viewsMap.getOrDefault(e.getId(), 0L));
            s.setConfirmedRequests(confirmedMap.getOrDefault(e.getId(), 0));
            return s;
        }).collect(Collectors.toList());

        if ("VIEWS".equalsIgnoreCase(params.getSort())) {
            dtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId) {
        // 1. Проверяем существование события и принадлежность его пользователю
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found."));

        // Проверка, является ли пользователь инициатором события
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User with id=" + userId +
                    " is not the initiator of event with id=" + eventId);
        }

        // 2. Получаем все запросы для этого события
        List<Request> requests = requestRepository.findByEventId(eventId);

        // 3. Маппинг и возврат DTO
        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
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

    @Override
    @Transactional
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long requestId) {
        // 1. Проверяем, что событие существует и принадлежит пользователю
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User with id=" + userId +
                    " is not the initiator of event with id=" + eventId);
        }

        // 2. Находим запрос на участие
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found."));

        // 3. Проверяем, что запрос принадлежит событию
        if (!request.getEvent().getId().equals(eventId)) {
            throw new ConflictException("Request with id=" + requestId + " is not for event id=" + eventId);
        }

        // 4. ИСПРАВЛЕННАЯ ПРОВЕРКА: нельзя отклонить уже подтвержденный запрос
        if (request.getStatus() == RequestStatus.CONFIRMED) {
            throw new ConflictException("Cannot reject already confirmed request");
        }

        // 5. Проверяем, что запрос еще не отклонен
        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new ConflictException("Request is already rejected");
        }

        // 6. Отклоняем запрос
        request.setStatus(RequestStatus.REJECTED);
        Request rejectedRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(rejectedRequest);
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

    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено.", log));

        if (oldEvent.getState() == EventState.PUBLISHED)
            throw new ConflictException("Only pending or canceled events can be changed.");

        Event newEvent = eventMapperStruct.toEvent(user, eventId, updateEventUserRequest);

        if (updateEventUserRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Категория с id = " + updateEventUserRequest.getCategoryId() + " не найдена.", log));

            newEvent.setCategory(category);
        }

        BeanUtils.copyProperties(newEvent, oldEvent, Reflection.getIgnoreProperties(newEvent));

        eventRepository.save(oldEvent);

        log.info("Пользователем обновлены данные события {}.", oldEvent);

        return eventMapper.toEventFullDto(oldEvent);
    }

    @Override
    public List<EventShortDto> findAllByUser(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        List<Event> eventListAll = eventRepository.findByInitiatorOrderByIdAsc(user);

        int toIndex = from + size;
        if (toIndex > eventListAll.size() - 1) toIndex = eventListAll.size();

        if (from > toIndex) {
            from = 0;
            toIndex = 0;
        }

        List<Event> eventList = new ArrayList<Event>(eventListAll.subList(from, toIndex));

        log.info("Получен список событий пользователя с id {} и параметрами: from = {}, size = {}.", userId, from, size);

        return eventList.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto findByUserAndEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        Event event = eventRepository.findByInitiatorAndId(user, eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found", log));

        log.info("Получены данные по событию c id = {} у пользователя с id = {}.", eventId, userId);

        return eventMapper.toEventFullDto(event);
    }
}
