package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.model.Category;

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
