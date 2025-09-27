package ru.practicum.category.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.validation.FieldDescription;
import ru.practicum.validation.Marker;

@Entity
@Table(name = "Categorys")
@Setter
@Getter
@EqualsAndHashCode(of = {"id", "name"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "При создании категории id формируется автоматически.", groups = Marker.OnCreate.class)
    @NotNull(message = "При обновлении данных о категории должен быть указан её id.",
            groups = {Marker.OnUpdate.class, Marker.OnDelete.class})
    @FieldDescription(value = "Уникальный идентификатор категории", changeByCopy = false)
    Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Наименование категории не может быть пустым.", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @FieldDescription("Наименование категории")
    @NotNull
    String name;
}