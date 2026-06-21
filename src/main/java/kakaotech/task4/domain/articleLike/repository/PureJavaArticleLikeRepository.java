package kakaotech.task4.domain.articleLike.repository;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.articleLike.entity.ArticleLike;
import kakaotech.task4.domain.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PureJavaArticleLikeRepository implements ArticleLikeRepository {
    private final List<ArticleLike> articleLikes = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public void save(ArticleLike articleLike) {
        articleLike.setArticleLikeId(sequence.getAndIncrement());
        articleLikes.add(articleLike);
    }

    @Override
    public Optional<ArticleLike> findByArticleAndUser(Article article, Member member) {
        return articleLikes.stream()
                .filter(like -> like.getArticle().equals(article))
                .filter(like -> like.getMember().equals(member))
                .findFirst();
    }

}
