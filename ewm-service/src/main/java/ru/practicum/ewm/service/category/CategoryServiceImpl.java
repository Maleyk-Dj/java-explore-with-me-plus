package ru.practicum.ewm.service.category;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.handler.exception.DuplicatedDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Reflection;

@Slf4j
@Service
@Qualifier("CategoryServiceImpl")
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toCategory(newCategoryDto);

        category = categoryRepository.save(category);

        log.info("Добавлена новая категория {}.", category);

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto update(Long categoryId, UpdateCategoryDto updateCategoryDto) {
        Category oldCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + categoryId + " не найдена.", log));

        Category newCategory = categoryMapper.toCategory(categoryId, updateCategoryDto);

        if (oldCategory.getName().equals(newCategory.getName()))
            return categoryMapper.toCategoryDto(oldCategory);  // исключаем избыточную запись в БД

        if (!StringUtils.isBlank(newCategory.getName())) {
            if (!isNameFree(newCategory.getName()))
                throw new DuplicatedDataException(String.format("Нельзя обновить данные категории с id %s " +
                        "по причине: нельзя использовать имя (регистр не важен), которое уже используется.", newCategory.getId()), log);
        }

        // обновляем содержимое
        BeanUtils.copyProperties(newCategory, oldCategory, Reflection.getIgnoreProperties(newCategory));

        categoryRepository.save(oldCategory);

        log.info("Обновлены данные категории {}.", oldCategory);

        return categoryMapper.toCategoryDto(oldCategory);
    }

    @Override
    public CategoryDto delete(Long categoryId) {
        Category removeCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + categoryId + " не найдена.", log));

        // todo Проверить, что нет связанных событий, прежде чем удалять. Смогу сделать когда эвенты появятся

        categoryRepository.delete(removeCategory);

        log.info("Удалена категория {}.", removeCategory);

        return categoryMapper.toCategoryDto(removeCategory);
    }

    private boolean isNameFree(final String name) {
        return categoryRepository.findByNameIgnoreCase(name).isEmpty();
    }
}
