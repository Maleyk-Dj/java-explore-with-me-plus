package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStats;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

@Component
public class StatisticsMapper {
    public Stat toStat(EndpointHitDto endpointHitDto) {
        Stat stat = new Stat();
        stat.setApp(endpointHitDto.getApp());
        stat.setUri(endpointHitDto.getUri());
        stat.setIp(endpointHitDto.getIp());
        stat.setCreated(endpointHitDto.getTimestamp());
        return stat;
    }

    public ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(viewStats.getApp(), viewStats.getUri(), viewStats.getHits());
    }
}
}
