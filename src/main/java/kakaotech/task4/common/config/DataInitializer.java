package kakaotech.task4.common.config;

import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.comment.entity.ArticleComment;
import kakaotech.task4.domain.comment.repository.ArticleCommentRepository;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository commentRepository;

    private static final int MEMBER_COUNT = 12;
    private static final int ARTICLE_COUNT = 30;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 서버 재시작마다 중복 삽입되는 것을 방지
        if (memberRepository.count() > 0) {
            return;
        }

        // 1) 회원 12명 생성
        List<Member> toSaveMembers = new ArrayList<>();
        for (int i = 1; i <= MEMBER_COUNT; i++) {
            toSaveMembers.add(
                    Member.builder()
                            .memberUuid("user_test" + i + "uuid")
                            .email("test" + i + "@kakaotech.com")
                            .password("Test" + i + "Hello1234!")
                            .nickname("test" + i)
                            .profileImageUrl("url" + i)
                            .build()
            );
        }
        List<Member> members = memberRepository.saveAll(toSaveMembers);

        // 2) 게시글 30개 생성 (작성자를 회원들에게 골고루 분배 → N+1이 또렷이 드러남)
        List<Article> toSaveArticles = new ArrayList<>();
        for (int i = 1; i <= ARTICLE_COUNT; i++) {
            Member author = members.get(i % MEMBER_COUNT); // 작성자 순환 분배
            toSaveArticles.add(
                    article(
                            author,
                            "article_uuid" + i,
                            author.getNickname() + " 게시글" + i,
                            author.getNickname() + "이(가) 작성한 " + i + "번째 게시글 내용"
                    )
            );
        }
        List<Article> articles = articleRepository.saveAll(toSaveArticles);

        // 3) 댓글 생성 (게시글당 0~3개, 작성자도 순환)
        List<ArticleComment> toSaveComments = new ArrayList<>();
        int commentSeq = 1;
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            int commentCountForArticle = i % 4; // 0,1,2,3 반복
            for (int c = 0; c < commentCountForArticle; c++) {
                Member commenter = members.get((i + c) % MEMBER_COUNT);
                ArticleComment comment = comment(
                        commenter,
                        article,
                        "comment_uuid" + commentSeq,
                        commenter.getNickname() + "이(가) " + article.getArticleUuid() + "에 작성한 댓글입니다."
                );
                toSaveComments.add(comment);
                article.increaseCommentCount();
                commentSeq++;
            }
        }
        commentRepository.saveAll(toSaveComments);
    }

    private Article article(Member member, String articleUuid, String title, String content) {
        return Article.builder()
                .articleUuid(articleUuid)
                .title(title)
                .content(content)
                .member(member)
                .build();
    }

    private ArticleComment comment(
            Member member,
            Article article,
            String commentUuid,
            String content
    ) {
        return ArticleComment.builder()
                .articleCommentUuid(commentUuid)
                .content(content)
                .member(member)
                .article(article)
                .build();
    }
}