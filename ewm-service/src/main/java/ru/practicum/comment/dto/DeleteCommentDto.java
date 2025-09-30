package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteCommentDto {
    @NotEmpty
    private List<Integer> commentsIds;
}
