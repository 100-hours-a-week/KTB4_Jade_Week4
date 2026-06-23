package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.entity.ArticlePhoto;
import kakaotech.task4.domain.article.repository.ArticlePhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ArticlePhotoService {
    private final ArticlePhotoRepository articlePhotoRepository;

    @Transactional
    public void savePhoto(String photoUrl, Article article) {
        Optional.ofNullable(photoUrl)
                .map(url -> {
                    String photoUuid = UuidCreator.create(UuidPrefix.ARTICLE_PHOTO);
                    return ArticlePhoto.of(photoUuid, url, article);
                })
                .ifPresent(articlePhotoRepository::save);
    }

    @Transactional
    public void updatePhoto(String photoUrl, Article article) {
        Optional.ofNullable(photoUrl)
                .ifPresent(url -> articlePhotoRepository.findByArticle(article)
                        .ifPresent(photo -> photo.updatePhotoUrl(url)));
    }

    public String findPhotoUrlByArticle(Article article) {
        return articlePhotoRepository.findByArticle(article)
                .map(ArticlePhoto::getPhotoUrl)
                .orElse(null);
    }
}