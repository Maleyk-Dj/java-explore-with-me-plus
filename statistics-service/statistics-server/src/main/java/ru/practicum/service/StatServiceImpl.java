package ru.practicum.service;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.StatisticsMapper;
import ru.practicum.model.Stat;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;
import ru.practicum.storage.StatRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final StatisticsMapper statisticsMapper;

    @Override
    @Transactional
    public void hit(EndpointHitDto request) {
        log.info("Saving hit for app: {}, uri: {}, ip: {}",
                request.getApp(), request.getUri(), request.getIp());

        Stat stat = statisticsMapper.toStat(request);
        statRepository.save(stat);
    }

    @Override
    public Collection<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            log.error("Validation error: End date {} is before start date {}.", end, start);
            throw new ValidationException();
        }

        var list = unique ? statRepository.getStatsUnique(start, end, uris) : statRepository.getStats(start, end, uris);

        return list.stream()
                .map(statisticsMapper::toViewStatsDto)
                .collect(Collectors.toList());
    }
}
