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

    // 생성할 데이터 규모 (값만 바꾸면 조절 가능)
    private static final int MEMBER_COUNT = 10;
    private static final int ARTICLE_COUNT = 100;
    private static final int MAX_COMMENTS_PER_ARTICLE = 5;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 서버 재시작마다 중복 삽입되는 것을 방지
        if (memberRepository.count() > 0) {
            return;
        }

        List<Member> members = createMembers();
        List<Article> articles = createArticles(members);
        createComments(members, articles);
    }

    private List<Member> createMembers() {
        List<Member> toSave = new ArrayList<>();
        for (int i = 1; i <= MEMBER_COUNT; i++) {
            toSave.add(
                    Member.builder()
                            .memberUuid("user_test" + i + "uuid")
                            .email("test" + i + "@kakaotech.com")
                            .password("Test" + i + "Hello1234!")
                            .nickname("test" + i)
                            .profileImageUrl("url" + i)
                            .build()
            );
        }
        return memberRepository.saveAll(toSave);
    }

    private List<Article> createArticles(List<Member> members) {
        List<Article> toSave = new ArrayList<>();
        for (int i = 1; i <= ARTICLE_COUNT; i++) {
            // 작성자를 회원들에게 순환 분배
            Member author = members.get((i - 1) % members.size());
            toSave.add(
                    Article.builder()
                            .articleUuid("article_uuid" + i)
                            .title(author.getNickname() + " 게시글" + i)
                            .content(author.getNickname() + "이(가) 작성한 " + i + "번째 게시글 내용")
                            .member(author)
                            .build()
            );
        }
        return articleRepository.saveAll(toSave);
    }

    private void createComments(List<Member> members, List<Article> articles) {
        List<ArticleComment> toSave = new ArrayList<>();
        int commentSeq = 1;

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            // 게시글마다 0 ~ MAX_COMMENTS_PER_ARTICLE 개의 댓글 (순환)
            int commentCount = i % (MAX_COMMENTS_PER_ARTICLE + 1);

            for (int c = 0; c < commentCount; c++) {
                // 댓글 작성자도 순환 분배
                Member commenter = members.get((i + c) % members.size());
                toSave.add(
                        ArticleComment.builder()
                                .articleCommentUuid("comment_uuid" + commentSeq)
                                .content(commenter.getNickname() + "이(가) "
                                        + article.getArticleUuid() + "에 작성한 댓글입니다.")
                                .member(commenter)
                                .article(article)
                                .build()
                );
                article.increaseCommentCount();
                commentSeq++;
            }
        }
        commentRepository.saveAll(toSave);
    }
}