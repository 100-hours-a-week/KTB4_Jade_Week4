package kakaotech.task4.domain.article.controller;

import jakarta.validation.Valid;
import kakaotech.task4.domain.article.api.ArticleApi;
import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/articles")
@AllArgsConstructor
public class ArticleController implements ArticleApi {
    private final ArticleService articleService;

    @PostMapping
    @Override
    public ResponseEntity<?> createArticle(@RequestHeader("Authorization") String userUuid,
                                           @Valid @RequestBody CreateArticleRequest request) {
        CreateArticleResponse response = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
