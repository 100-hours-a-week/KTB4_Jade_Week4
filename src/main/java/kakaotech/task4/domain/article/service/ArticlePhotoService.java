package kakaotech.task4.domain.article.service;

import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.entity.ArticlePhoto;
import kakaotech.task4.domain.article.repository.ArticlePhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ArticlePhotoService {
    private final ArticlePhotoRepository articlePhotoRepository;

    public void savePhoto(String photoUrl, Article article) {
        Optional.ofNullable(photoUrl)
                .map(url -> {
                    String photoUuid = UuidCreator.create(UuidPrefix.ARTICLE_PHOTO);
                    return ArticlePhoto.of(photoUuid, url, article);
                })
                .ifPresent(articlePhotoRepository::save);
    }

    public void updatePhoto(String photoUrl, Article article) {
        Optional.ofNullable(photoUrl)
                .ifPresent(url -> articlePhotoRepository.findByArticle(article)
                        .ifPresent(photo -> photo.updatePhotoUrl(url)));
    }

}