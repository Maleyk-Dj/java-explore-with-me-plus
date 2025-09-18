package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.UpdateCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.mapper.IgnoreUnmappedMapperConfig;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, config = IgnoreUnmappedMapperConfig.class)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "newCategoryDto.name", target = "name")
    Category toCategory(NewCategoryDto newCategoryDto);

    @Mapping(source = "categoryId", target = "id")
    @Mapping(source = "updateCategoryDto.name", target = "name")
    Category toCategory(Long categoryId, UpdateCategoryDto updateCategoryDto);

    CategoryDto toCategoryDto(Category category);
}
