package ru.practicum.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.validation.EventDate;
import ru.practicum.validation.FieldDescription;
import ru.practicum.validation.Marker;

import java.time.Instant;

import static ru.practicum.util.Constants.*;

@Entity
@Table(name = "Events")
@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "При создании запроса id формируется автоматически.", groups = Marker.OnCreate.class)
    @NotNull(message = "При обновлении данных о запросе должен быть указан его id.",
            groups = {Marker.OnUpdate.class, Marker.OnDelete.class})
    @FieldDescription(value = "Уникальный идентификатор события", changeByCopy = false)
    Long id;

    @Size(min = LENGTH_ANNOTATION_EVENT_MIN, max = LENGTH_ANNOTATION_EVENT_MAX, message = "Длина краткого описания события не прошла валидацию.")
    @Column(length = LENGTH_ANNOTATION_EVENT_MAX)
    @NotBlank(message = "Краткое описание события не может быть пустым.", groups = Marker.OnCreate.class)
    String annotation;

    @NotNull(message = "Категория должна быть указана при создании события.", groups = Marker.OnCreate.class)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;  // категория к которой относится событие

    @Size(min = LENGTH_DESCRIPTION_EVENT_MIN, max = LENGTH_DESCRIPTION_EVENT_MAX, message = "Длина описания события не прошла валидацию.")
    @Column(length = LENGTH_DESCRIPTION_EVENT_MAX)
    @NotBlank(message = "Описание события не может быть пустым.", groups = Marker.OnCreate.class)
    String description;

    @EventDate
    Instant eventDate;

    @NotNull(message = "Нужно указать широту места события.", groups = Marker.OnCreate.class)
    Float lat;

    @NotNull(message = "Нужно указать долготу места события.", groups = Marker.OnCreate.class)
    Float lon;

    Boolean paid;

    Long participantLimit;

    Boolean requestModeration;

    @Size(min = LENGTH_TITLE_EVENT_MIN, max = LENGTH_TITLE_EVENT_MAX, message = "Длина заголовка события не прошла валидацию.")
    @Column(length = LENGTH_TITLE_EVENT_MAX)
    String title;

    @NotNull(message = "Нужно указать инициатора события.", groups = Marker.OnCreate.class)
    Long initiator; // todo User
}
