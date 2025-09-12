package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.exception.StatsClientException;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StatClient {
    private final RestClient restClient;

    @Autowired
    public StatClient(@Value("${stats-server.url:http://localhost:9090}") String statsUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsUrl)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            String errorMessage = String.format("Ошибка сервиса статистики: %d %s",
                                    response.getStatusCode().value(), response.getStatusText());
                            log.error(errorMessage);
                            throw new StatsClientException(errorMessage);
                        })
                .build();
    }

    public void hit(EndpointHitDto endpointHitDto) {
        try {
            restClient.post()
                    .uri("/hit")
                    .body(endpointHitDto)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Статистика успешно отправлена: app={}, uri={}, ip={}",
                    endpointHitDto.getApp(), endpointHitDto.getUri(), endpointHitDto.getIp());
        } catch (Exception e) {
            log.error("Ошибка при сохранении статистики: {}, {}", endpointHitDto, e.getMessage());
            throw new StatsClientException("Ошибка при отправке статистики", e);
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter dateTimeFormated = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        validateGetStatsParam(start, end);

        try {
            log.info("Запрос статистики: start={}, end={}, uris={}, unique={}",
                    start, end, uris, unique);

            return restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start.format(dateTimeFormated))
                            .queryParam("end", end.format(dateTimeFormated))
                            .queryParamIfPresent("uris", Optional.ofNullable(uris).filter(list -> !list.isEmpty()))
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                    });

        } catch (Exception e) {
            log.error("Ошибка при получении статистики: start={}, end={}, uris={}, unique={}, error={}",
                    start, end, uris, unique, e.getMessage());
            throw new StatsClientException("Ошибка при получении статистики");
        }
    }

    //валидация параметров запроса статистики
    private void validateGetStatsParam(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new IllegalArgumentException("Дата начала не может быть нулевой");
        }
        if (end == null) {
            throw new IllegalArgumentException("Дата окончания не может быть нулевой");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }
    }
}
