package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.DeleteCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Integer userId, Integer eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Integer userId, Integer commentId, UpdateCommentDto updateCommentDto);

    void deleteCommentByUser(Integer userId, Integer commentId);

    CommentDto getCommentById(Integer commentId);

    List<CommentDto> getCommentsByUserId(Integer userId);

    void deleteCommentByAdmin(DeleteCommentDto deleteCommentsDto);

    List<CommentDto> getComments(String content, Integer userId, Integer eventId,
                                 String rangeStart, String rangeEnd, Integer from, Integer size);
}
