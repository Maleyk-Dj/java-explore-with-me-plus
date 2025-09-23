package ru.practicum.ewm.service.category;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.category.UpdateCategoryDto;

public interface CategoryService {
    public CategoryDto add(NewCategoryDto newCategoryDto);

    public CategoryDto update(Long categoryId, UpdateCategoryDto updateCategoryDto);

    public CategoryDto delete(Long categoryId);
}
