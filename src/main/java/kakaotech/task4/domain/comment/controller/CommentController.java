package kakaotech.task4.domain.comment.controller;

import jakarta.validation.Valid;
import kakaotech.task4.domain.comment.api.CommentApi;
import kakaotech.task4.domain.comment.dto.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.CreateCommentResponse;
import kakaotech.task4.domain.comment.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/articles/{article-uuid}/comment")
public class CommentController implements CommentApi {
    private final CommentService commentService;

    @PostMapping
    @Override
    public ResponseEntity<?> createComment(
            @RequestHeader("Authorization") String userUuid,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = commentService.createComment(userUuid, articleUuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}