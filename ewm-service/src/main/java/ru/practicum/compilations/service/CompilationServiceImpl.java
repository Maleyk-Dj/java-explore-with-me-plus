package ru.practicum.compilations.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.params.PublicCompilationsParams;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.handling.exception.NotFoundException;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper; // <-- Внедряем компонент


    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> findCompilations(PublicCompilationsParams params) {
        log.info("Поиск подборок событий: закрепленные={}, from={}, size={}",
                params.getPinned(), params.getFrom(), params.getSize());

        PageRequest pageRequest = PageRequest.of(
                params.getFrom() / params.getSize(),
                params.getSize()
        );

        List<Compilation> compilations;
        if (params.getPinned() != null) {
            compilations = compilationRepository.findByPinned(params.getPinned(), pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }

        return compilations.stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto findCompilationById(Long compId) {
        log.info("Поиск подборки по ID: {}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto dto) {
        log.info("Добавление новой подборки: {}", dto);

        Set<Event> events = getEventsFromIds(dto.getEvents());
        Compilation compilation = compilationMapper.toCompilation(dto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Подборка успешно добавлена id: {}", savedCompilation.getId());
        return compilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        log.info("Обновление подборки: ID={}, новые данные={}", compId, dto);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        Set<Event> events = null;
        if (dto.getEvents() != null) {
            events = getEventsFromIds(dto.getEvents());
        }

        CompilationMapper.updateCompilationFromDto(dto, compilation, events);
        Compilation updatedCompilation = compilationRepository.save(compilation);

        log.info("Подборка с ID {} успешно обновлена", compId);
        return compilationMapper.toCompilationDto(updatedCompilation);
    }

    @Override
    @Transactional
    public void removeCompilation(Long compId) {
        log.info("Удаление подборки: ID={}", compId);

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }

        compilationRepository.deleteById(compId);
        log.info("Подборка с ID {} успешно удалена", compId);
    }

    private Set<Event> getEventsFromIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Set.of();
        }
        // 1. Очистка списка от null и дубликатов, чтобы избежать NPE в репозитории
        Set<Long> uniqueEventIds = new HashSet<>(eventIds);
        uniqueEventIds.remove(null);

        if (uniqueEventIds.isEmpty()) {
            return Set.of();
        }

        // 2. Получение найденных событий
        List<Event> foundEvents = eventRepository.findAllById(uniqueEventIds);

        // 3. КРИТИЧЕСКАЯ ПРОВЕРКА (Исправляет логическую ошибку):
        // Если количество найденных событий не совпадает с запрошенным, выбрасываем 404.
        if (foundEvents.size() != uniqueEventIds.size()) {

            Set<Long> foundIds = foundEvents.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());

            String missingIds = uniqueEventIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            // Ваш @ControllerAdvice преобразует это в 404 NOT FOUND
            throw new NotFoundException("Events with IDs not found: " + missingIds);
        }

        return new HashSet<>(foundEvents);
    }
}