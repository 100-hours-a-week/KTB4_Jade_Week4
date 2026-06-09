package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArticleLikeFacadeService {
    private final UserService userService;
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;

    public ArticleLikeResponse like(String userUuid, String articleUuid) {
        User user = findUserByUuid(userUuid);
        Article article = articleService.findArticleByUuid(articleUuid);
        article.increaseLikedCount();
        return articleLikeService.like(user, article);
    }

    public ArticleLikeResponse unlike(String userUuid, String articleUuid) {
        User user = findUserByUuid(userUuid);
        Article article = articleService.findArticleByUuid(articleUuid);
        article.decreaseLikedCount();
        return articleLikeService.unlike(user, article);
    }

    private User findUserByUuid(String userUuid) {
        return userService.findByUuid(userUuid)
                .orElseThrow(() -> new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR));
    }
}