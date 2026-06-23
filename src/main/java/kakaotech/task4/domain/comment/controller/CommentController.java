package kakaotech.task4.domain.comment.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.comment.api.CommentApi;
import kakaotech.task4.domain.comment.dto.req.CreateCommentRequest;
import kakaotech.task4.domain.comment.dto.req.UpdateCommentRequest;
import kakaotech.task4.domain.comment.dto.res.CreateCommentResponse;
import kakaotech.task4.domain.comment.service.ArticleCommentService;
import kakaotech.task4.domain.member.entity.Member;
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
            @CurrentMember Member member,
            @PathVariable("article-uuid") String articleUuid,
            @Valid @RequestBody CreateCommentRequest request) {
        CreateCommentResponse response = articleCommentService.createComment(member, articleUuid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{comment-uuid}")
    @Override
    public ResponseEntity<?> updateComment(
            @CurrentMember Member member,
            @PathVariable("article-uuid") String articleUuid,
            @PathVariable("comment-uuid") String commentUuid,
            @Valid @RequestBody UpdateCommentRequest request) {
        articleCommentService.updateComment(member, articleUuid, commentUuid, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{comment-uuid}")
    @Override
    public ResponseEntity<?> deleteComment(
            @CurrentMember Member member,
            @PathVariable("article-uuid") String articleUuid,
            @PathVariable("comment-uuid") String commentUuid) {
        articleCommentService.deleteComment(member, articleUuid, commentUuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}