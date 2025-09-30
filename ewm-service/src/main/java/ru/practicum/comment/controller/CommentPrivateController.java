package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive Integer userId,
                                    @RequestParam @Positive Integer eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {

        return commentService.createComment(userId, eventId, newCommentDto);

    }

    @PatchMapping("/users/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable @Positive Integer userId,
                                    @PathVariable @Positive Integer commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        return commentService.updateComment(userId, commentId, updateCommentDto);

    }

    @DeleteMapping("/users/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Integer userId,
                              @PathVariable @Positive Integer commentId) {

        commentService.deleteCommentByUser(userId, commentId);

    }

    @GetMapping("/users/{userId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByUserId(@PathVariable @Positive Integer userId) {

        return commentService.getCommentsByUserId(userId);

    }
}
