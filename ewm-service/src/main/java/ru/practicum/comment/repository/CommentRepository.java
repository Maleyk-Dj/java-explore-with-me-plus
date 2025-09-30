package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByUserIdAndId(Integer userId, Integer commentId);

    List<Comment> findByUserId(Integer userId);

    List<Comment> findByIdIn(List<Integer> commentIds);

    void deleteByIdIn(List<Integer> commentIds);

    @Query("SELECT c FROM Comment c " +
            "WHERE (:content IS NULL OR LOWER(CAST(c.content AS string)) LIKE LOWER(CONCAT('%', CAST(:content AS string), '%'))) " +
            "AND (:userId IS NULL OR c.user.id = :userId) " +
            "AND (:eventId IS NULL OR c.event.id = :eventId) " +
            "AND c.created >= :rangeStart " +
            "AND c.created <= :rangeEnd")
    Page<Comment> getComments(@Param("content") String content,
                              @Param("userId") Integer userId,
                              @Param("eventId") Integer eventId,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd,
                              Pageable pageable);
}
