package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * Count confirmed requests for a single event.
     */
    @Query("select count(r) from Request r where r.event.id = :eventId and r.status = 'CONFIRMED'")
    Integer countConfirmedByEventId(@Param("eventId") Long eventId);

    /**
     * Batch count: returns pairs (eventId, cnt) for the provided eventIds.
     * Projection ConfirmedCount has getters getEventId() and getCnt().
     */
    @Query("select r.event.id as eventId, count(r) as cnt " +
            "from Request r " +
            "where r.event.id in :eventIds and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    List<ConfirmedCount> countConfirmedForEventIds(@Param("eventIds") List<Long> eventIds);
}