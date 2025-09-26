package ru.practicum.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

@Component
public class CategoryMapper {

    /**
     * Преобразует NewCategoryDto в сущность Category.
     * @param newCategoryDto DTO с данными для новой категории.
     * @return Сущность Category.
     */
    public Category toCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            return null;

        }

        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    /**
     * Преобразует сущность Category в CategoryDto.
     * @param category Сущность Category.
     * @return DTO с полной информацией о категории.
     */
    public CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}