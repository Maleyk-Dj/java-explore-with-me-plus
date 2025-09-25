package ru.practicum.request;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    // Метод для получения всех запросов пользователя
    List<ParticipationRequestDto> getUserRequests(Long userId);

    // Метод для отмены запроса
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
