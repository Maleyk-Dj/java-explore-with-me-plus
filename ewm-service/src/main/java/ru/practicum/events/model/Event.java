package ru.practicum.events.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.events.enums.EventState;
import ru.practicum.ewm.user.model.User;
import ru.practicum.validation.FieldDescription;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @FieldDescription(value = "Уникальный идентификатор собятия", changeByCopy = false)
    private Long id;

    @Column(nullable = false, length = 2000)
    @FieldDescription(value = "Аннотация")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @FieldDescription(value = "Категория события")
    private Category category;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;

    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(nullable = false, length = 7000)
    @FieldDescription(value = "Описание")
    private String description;

    @Column(name = "event_date", nullable = false)
    @FieldDescription(value = "Дата проведения")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    @FieldDescription(value = "Пользователь")
    private User initiator;

    @Column(name = "location_lat")
    @FieldDescription(value = "Д")
    private Float locationLat;

    @Column(name = "location_lon")
    @FieldDescription(value = "Ш")
    private Float locationLon;

    @Column(nullable = false)
    @FieldDescription(value = "Утверждение")
    private Boolean paid = false;

    @Column(name = "participant_limit")
    @FieldDescription(value = "Ограничение по колву")
    private Integer participantLimit = 0;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    @FieldDescription(value = "Модерация")
    private Boolean requestModeration = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @FieldDescription(value = "Состояние")
    private EventState state = EventState.PENDING;

    @Column(nullable = false, length = 120)
    @FieldDescription(value = "Заголовок")
    private String title;

    private Long views = 0L;
}
