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

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository commentRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 서버 재시작마다 중복 삽입되는 것을 방지
        if (memberRepository.count() > 0) {
            return;
        }

        List<Member> members = memberRepository.saveAll(List.of(
                Member.builder()
                        .memberUuid("user_test1uuid")
                        .email("test1@kakaotech.com")
                        .password("Test1Hello1234!")
                        .nickname("test1")
                        .profileImageUrl("url1")
                        .build(),

                Member.builder()
                        .memberUuid("user_test2uuid")
                        .email("test2@kakaotech.com")
                        .password("Test2Hello1234!")
                        .nickname("test2")
                        .profileImageUrl("url2")
                        .build()
        ));

        Member member1 = members.get(0);
        Member member2 = members.get(1);

        List<Article> articles = articleRepository.saveAll(List.of(
                article(member1, "article_uuid1", "test1 게시글1", "test1 첫 번째 내용"),
                article(member1, "article_uuid2", "test1 게시글2", "test1 두 번째 내용"),
                article(member1, "article_uuid3", "test1 게시글3", "test1 세 번째 내용"),
                article(member1, "article_uuid4", "test1 게시글4", "test1 네 번째 내용"),
                article(member1, "article_uuid5", "test1 게시글5", "test1 다섯 번째 내용"),
                article(member2, "article_uuid6", "test2 게시글1", "test2 첫 번째 내용"),
                article(member2, "article_uuid7", "test2 게시글2", "test2 두 번째 내용"),
                article(member2, "article_uuid8", "test2 게시글3", "test2 세 번째 내용"),
                article(member2, "article_uuid9", "test2 게시글4", "test2 네 번째 내용"),
                article(member2, "article_uuid10", "test2 게시글5", "test2 다섯 번째 내용"),
                article(member1, "article_uuid11", "test1 게시글6", "test1 여섯 번째 내용"),
                article(member1, "article_uuid12", "test1 게시글7", "test1 일곱 번째 내용"),
                article(member2, "article_uuid13", "test2 게시글6", "test2 여섯 번째 내용"),
                article(member2, "article_uuid14", "test2 게시글7", "test2 일곱 번째 내용"),
                article(member1, "article_uuid15", "test1 게시글8", "test1 여덟 번째 내용")
        ));

        List<ArticleComment> comments = List.of(
                comment(member1, articles.get(0), "comment_uuid1", "test1이 article_uuid1에 작성한 댓글입니다."),
                comment(member2, articles.get(0), "comment_uuid2", "test2가 article_uuid1에 작성한 댓글입니다."),
                comment(member1, articles.get(1), "comment_uuid3", "test1이 article_uuid2에 작성한 댓글입니다."),
                comment(member2, articles.get(1), "comment_uuid4", "test2가 article_uuid2에 작성한 댓글입니다."),
                comment(member1, articles.get(2), "comment_uuid5", "test1이 article_uuid3에 작성한 댓글입니다."),
                comment(member1, articles.get(3), "comment_uuid6", "test1이 article_uuid4에 작성한 댓글입니다."),
                comment(member1, articles.get(4), "comment_uuid7", "test1이 article_uuid5에 작성한 댓글입니다."),
                comment(member2, articles.get(4), "comment_uuid8", "test2가 article_uuid5에 작성한 댓글입니다."),
                comment(member2, articles.get(5), "comment_uuid9", "test2가 article_uuid6에 작성한 댓글입니다.")
        );

        // 댓글 데이터 기준으로 댓글 수를 증가시킴
        comments.forEach(comment ->
                comment.getArticle().increaseCommentCount()
        );

        commentRepository.saveAll(comments);
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