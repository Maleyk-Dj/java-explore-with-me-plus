package ru.practicum.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatService;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Контроллер сущности Stat.
 */
@Validated
@RestController
public class StatController {
    private final StatService statService;

    private static final Logger log = LoggerFactory.getLogger(StatController.class);

    @Autowired
    public StatController(@Qualifier("StatServiceImpl") StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto hit(@RequestBody EndpointHitDto request) {
        return statService.hit(request);
    }

    @GetMapping("/stats")
    public Collection<ViewStatsDto> getStats(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                             @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                             @RequestParam(name = "uris", required = false) List<String> uris,
                                             @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }
}
