package ru.practicum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.StatisticsMapper;
import ru.practicum.model.Stat;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;
import ru.practicum.storage.StatRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Qualifier("StatServiceImpl")
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    private static final Logger log = LoggerFactory.getLogger(StatServiceImpl.class);

    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @Override
    public EndpointHitDto hit(EndpointHitDto request) {
        Stat stat = new Stat();
        stat.setApp(request.getApp());
        stat.setUri(request.getUri());
        stat.setIp(request.getIp());
        stat.setCreated(request.getTimestamp());

        stat = statRepository.save(stat);

        request.setId(stat.getId());

        return request;
    }

    @Override
    public Collection<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        var list = unique ? statRepository.getStatsUnique(start, end, uris) : statRepository.getStats(start, end, uris);

        return list.stream()
                .map(StatisticsMapper::toViewStatsDto)
                .toList();
    }
}
