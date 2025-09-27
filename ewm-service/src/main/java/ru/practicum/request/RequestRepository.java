package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select count(r) from Request r where r.event.id = :eventId and r.status = 'CONFIRMED'")
    Integer countConfirmedByEventId(@Param("eventId") Long eventId);

    @Query("select r.event.id as eventId, count(r) as cnt " +
            "from Request r " +
            "where r.event.id in :eventIds and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    List<ConfirmedCount> countConfirmedForEventIds(@Param("eventIds") List<Long> eventIds);

    // Находит все заявки для определенного пользователя
    List<Request> findByRequesterId(Long requesterId);

    // Находит конкретную заявку для определенного пользователя и заявки
    Optional<Request> findByRequesterIdAndId(Long requesterId, Long requestId);

    // Находит все заявки на участие в событии
    List<Request> findByEventId(Long eventId);

    // Подсчитывает количество подтвержденных заявок на участие в событии
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    // Проверка существования запроса
    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

}