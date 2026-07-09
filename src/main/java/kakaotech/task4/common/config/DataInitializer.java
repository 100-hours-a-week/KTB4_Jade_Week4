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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final int MEMBER_COUNT = 10;
    private static final int ARTICLE_COUNT = 100;
    private static final int MAX_COMMENTS_PER_ARTICLE = 5;

    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (memberRepository.count() > 0) {
            return;
        }

        List<Member> members = createMembers();
        List<Article> articles = createArticles(members);
        createComments(members, articles);
    }

    private List<Member> createMembers() {
        List<Member> toSave = new ArrayList<>();

        toSave.add(
                Member.builder()
                        .memberUuid("jade_uuid")
                        .email("jade@naver.com")
                        .password(passwordEncoder.encode("Jade1234!"))
                        .nickname("Jade")
                        .profileImageUrl("jade-profile-url")
                        .build()
        );

        for (int i = 1; i <= MEMBER_COUNT; i++) {
            toSave.add(
                    Member.builder()
                            .memberUuid("user_test" + i + "uuid")
                            .email("test" + i + "@kakaotech.com")
                            .password(passwordEncoder.encode("Test" + i + "Hello1234!"))
                            .nickname("test" + i)
                            .profileImageUrl("url" + i)
                            .build()
            );
        }

        return memberRepository.saveAll(toSave);
    }

    private List<Article> createArticles(List<Member> members) {
        List<Article> toSave = new ArrayList<>();

        Member jade = members.stream()
                .filter(member -> member.getEmail().equals("jade@naver.com"))
                .findFirst()
                .orElseThrow();

        toSave.add(
                Article.builder()
                        .articleUuid("jade_article_uuid_1")
                        .title("안녕하세요, Jade의 첫 게시글입니다.")
                        .content("테스트 계정 Jade가 작성한 첫 번째 게시글입니다.")
                        .member(jade)
                        .build()
        );

        toSave.add(
                Article.builder()
                        .articleUuid("jade_article_uuid_2")
                        .title("오늘의 점심 추천 받아요")
                        .content("회사 근처에서 먹을 만한 점심 메뉴를 추천해주세요.")
                        .member(jade)
                        .build()
        );

        toSave.add(
                Article.builder()
                        .articleUuid("jade_article_uuid_3")
                        .title("프로젝트 진행 상황 공유")
                        .content("게시글, 댓글, 마이페이지 기능 구현을 진행 중입니다.")
                        .member(jade)
                        .build()
        );

        for (int i = 1; i <= ARTICLE_COUNT - 3; i++) {
            Member author = members.get((i - 1) % members.size());

            toSave.add(
                    Article.builder()
                            .articleUuid("article_uuid" + i)
                            .title(author.getNickname() + " 게시글 " + i)
                            .content(author.getNickname() + "이(가) 작성한 " + i + "번째 게시글 내용")
                            .member(author)
                            .build()
            );
        }

        return articleRepository.saveAll(toSave);
    }

    private void createComments(List<Member> members, List<Article> articles) {
        List<ArticleComment> toSave = new ArrayList<>();

        Member jade = members.stream()
                .filter(member -> member.getEmail().equals("jade@naver.com"))
                .findFirst()
                .orElseThrow();

        Article jadeFirstArticle = articles.stream()
                .filter(article -> article.getArticleUuid().equals("jade_article_uuid_1"))
                .findFirst()
                .orElseThrow();

        Article jadeSecondArticle = articles.stream()
                .filter(article -> article.getArticleUuid().equals("jade_article_uuid_2"))
                .findFirst()
                .orElseThrow();

        Member testUser1 = members.stream()
                .filter(member -> member.getEmail().equals("test1@kakaotech.com"))
                .findFirst()
                .orElseThrow();

        Member testUser2 = members.stream()
                .filter(member -> member.getEmail().equals("test2@kakaotech.com"))
                .findFirst()
                .orElseThrow();

        toSave.add(
                ArticleComment.builder()
                        .articleCommentUuid("jade_comment_uuid_1")
                        .content("Jade님 첫 게시글 반갑습니다!")
                        .member(testUser1)
                        .article(jadeFirstArticle)
                        .build()
        );

        toSave.add(
                ArticleComment.builder()
                        .articleCommentUuid("jade_comment_uuid_2")
                        .content("앞으로도 자주 글 올려주세요.")
                        .member(testUser2)
                        .article(jadeFirstArticle)
                        .build()
        );

        toSave.add(
                ArticleComment.builder()
                        .articleCommentUuid("jade_comment_uuid_3")
                        .content("저는 김치찌개 추천합니다.")
                        .member(jade)
                        .article(jadeSecondArticle)
                        .build()
        );

        int commentSeq = 1;

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);

            if (article.getArticleUuid().startsWith("jade_article_uuid")) {
                continue;
            }

            int commentCount = i % (MAX_COMMENTS_PER_ARTICLE + 1);

            for (int c = 0; c < commentCount; c++) {
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

                commentSeq++;
            }
        }

        commentRepository.saveAll(toSave);
    }
}