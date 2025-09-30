package ru.practicum.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentDto commentToDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUser(comment.getUser().getId());
        dto.setEvent(comment.getEvent().getId());

        return dto;
    }

    //опционально
    public List<CommentDto> commentsToDtos(List<Comment> comments) {
        if (comments == null) {
            return List.of();
        }

        return comments.stream()
                .map(this::commentToDto)
                .collect(Collectors.toList());
    }
}
