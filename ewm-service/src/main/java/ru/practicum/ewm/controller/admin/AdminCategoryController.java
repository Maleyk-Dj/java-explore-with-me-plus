package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.service.category.CategoryService;
import ru.practicum.validation.Marker;

import static ru.practicum.util.Constants.PATH_VARIABLE_ID;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(@Qualifier("CategoryServiceImpl") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public CategoryDto add(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        log.info("Получен запрос: Добавить новую категорию с name {}", newCategoryDto.getName());

        return categoryService.add(newCategoryDto);
    }

    @PatchMapping("/{id}")
    @Validated(Marker.OnUpdate.class)
    public CategoryDto update(@PathVariable(name = PATH_VARIABLE_ID) @Positive(groups = Marker.OnUpdate.class) Long categoryId,
                              @RequestBody @Valid UpdateCategoryDto updateCategoryDto) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        log.info("Получен запрос: Обновить данные категории с id {}. Установить name {}", categoryId, updateCategoryDto.getName());

        return categoryService.update(categoryId, updateCategoryDto);
    }

    @DeleteMapping("/{id}")
    @Validated(Marker.OnDelete.class)
    public CategoryDto delete(@PathVariable(name = PATH_VARIABLE_ID) @Positive(groups = Marker.OnDelete.class) Long categoryId) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей
        log.info("Получен запрос: Удалить категорию с id {}.", categoryId);

        return categoryService.delete(categoryId);
    }
}
