package kakaotech.task4.domain.article.service;

import kakaotech.task4.domain.article.dto.req.CreateArticleRequest;
import kakaotech.task4.domain.article.dto.res.CreateArticleResponse;
import kakaotech.task4.domain.article.entity.Article;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArticleFacadeService {
    private final ArticleService articleService;
    private final ArticlePhotoService articlePhotoService;

    public CreateArticleResponse createArticle(String userUuid, CreateArticleRequest request) {
        Article article = articleService.createArticle(userUuid, request);
        articlePhotoService.savePhoto(request.imageUrl(), article);
        return CreateArticleResponse.from(article.getArticleUuid());
    }

}