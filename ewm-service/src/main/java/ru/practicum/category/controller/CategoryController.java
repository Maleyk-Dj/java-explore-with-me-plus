package ru.practicum.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.validation.Marker;

import static ru.practicum.util.Constants.PATH_VARIABLE_ID;

@Validated
@RestController
@RequestMapping("/admin/categories")
public class CategoryController {
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public CategoryDto add(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        log.info("Получен запрос: Добавить новую категорию с name {}", newCategoryDto.getName());

        // return CategoryService.add(newCategoryDto); todo

        return null;
    }

    @PatchMapping("/{id}")
    @Validated(Marker.OnUpdate.class)
    public CategoryDto update(@PathVariable(name = PATH_VARIABLE_ID) @Positive(groups = Marker.OnUpdate.class) Long categoryId,
                              @RequestBody @Valid UpdateCategoryDto updateCategoryDto) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        log.info("Получен запрос: Обновить данные категории с id {}. Установить name {}", categoryId, updateCategoryDto.getName());

        // return CategoryService.update(updateCategoryDto); todo

        return null;
    }

    @DeleteMapping("/{id}")
    @Validated(Marker.OnDelete.class)
    public CategoryDto delete(@PathVariable(name = PATH_VARIABLE_ID) @Positive(groups = Marker.OnDelete.class) Long categoryId) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей
        log.info("Получен запрос: Удалить категорию с id {}.", categoryId);

        // return CategoryService.delete(categoryId); todo

        return null;
    }
}
