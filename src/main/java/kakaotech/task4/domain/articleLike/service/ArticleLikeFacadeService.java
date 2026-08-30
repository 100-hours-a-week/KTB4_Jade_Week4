package kakaotech.task4.domain.articleLike.service;

import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.service.ArticleService;
import kakaotech.task4.domain.articleLike.dto.ArticleLikeResponse;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class ArticleLikeFacadeService {
    private final ArticleService articleService;
    private final ArticleLikeService articleLikeService;
    private final MemberService memberService;

    @Transactional
    public ArticleLikeResponse like(String memberUuid, String articleUuid) {
        Member member = findCurrentMember(memberUuid);
        Article article = articleService.findArticleByUuid(articleUuid);
        return articleLikeService.like(member, article);
    }

    @Transactional
    public ArticleLikeResponse unlike(String memberUuid, String articleUuid) {
        Member member = findCurrentMember(memberUuid);
        Article article = articleService.findArticleByUuid(articleUuid);
        return articleLikeService.unlike(member, article);
    }

    private Member findCurrentMember(String memberUuid) {
        return memberService.findByUuid(memberUuid, AuthExceptionCode.UNAUTHORIZED);
    }
}
