package kakaotech.task4.domain.comment.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.comment.api.CommentApi;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.req.UpdateCommentRequest;
import kakaotech.task4.domain.comment.dto.res.CreateCommentResponse;
import kakaotech.task4.domain.comment.service.ArticleCommentService;
import kakaotech.task4.common.response.ApiResponse;
import kakaotech.task4.common.security.AuthenticatedMember;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/articles/{article-uuid}/comments")
public class CommentController implements CommentApi {
    private final ArticleCommentService articleCommentService;

    @PostMapping
    @Override
    public ResponseEntity<?> createComment(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = articleCommentService.createComment(member.memberUuid(), articleUuid, request);
        return ApiResponse.created(response).toEntity();
    }

    @PutMapping("/{comment-uuid}")
    @Override
    public ResponseEntity<?> updateComment(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid,
            @PathVariable("comment-uuid") String commentUuid,
            @Valid @RequestBody UpdateCommentRequest request) {
        articleCommentService.updateComment(member.memberUuid(), articleUuid, commentUuid, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{comment-uuid}")
    @Override
    public ResponseEntity<?> deleteComment(
            @CurrentMember AuthenticatedMember member,
            @PathVariable("article-uuid") String articleUuid,
            @PathVariable("comment-uuid") String commentUuid) {
        articleCommentService.deleteComment(member.memberUuid(), articleUuid, commentUuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}