package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Stat;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {
    Collection<Stat> findByCreatedBetween(LocalDateTime start, LocalDateTime end);

    Collection<Stat> findByCreatedBetweenAndUriIn(LocalDateTime start, LocalDateTime end, Collection<String> uris);

    @Query("SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(s)) " +
            "FROM Stat s " +
            "WHERE s.created BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR s.uri IN :uris) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s) DESC")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat s " +
            "WHERE s.created BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR s.uri IN :uris) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
