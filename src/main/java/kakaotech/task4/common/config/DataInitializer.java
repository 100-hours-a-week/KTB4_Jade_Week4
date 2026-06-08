package kakaotech.task4.common.config;

import jakarta.annotation.PostConstruct;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
import kakaotech.task4.domain.comment.entity.Comment;
import kakaotech.task4.domain.comment.repository.CommentRepository;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @PostConstruct
    public void init() {
        User user1 = User.builder()
                .userId(1000).userUuid("user_test1uuid").email("test1@kakaotech.com")
                .password("Test1Hello1234!").nickname("test1").profileImageUrl("url1")
                .build();

        User user2 = User.builder()
                .userId(1001).userUuid("user_test2uuid").email("test2@kakaotech.com")
                .password("Test2Hello1234!").nickname("test2").profileImageUrl("url2")
                .build();

        userRepository.addAll(List.of(user1, user2));

        Article article0 = Article.builder().articleId(1000).articleUuid("article_uuid1").title("test1 게시글1").content("test1 첫 번째 내용").user(user1).build();
        Article article1 = Article.builder().articleId(1001).articleUuid("article_uuid2").title("test1 게시글2").content("test1 두 번째 내용").user(user1).build();
        Article article2 = Article.builder().articleId(1002).articleUuid("article_uuid3").title("test1 게시글3").content("test1 세 번째 내용").user(user1).build();
        Article article3 = Article.builder().articleId(1003).articleUuid("article_uuid4").title("test1 게시글4").content("test1 네 번째 내용").user(user1).build();
        Article article4 = Article.builder().articleId(1004).articleUuid("article_uuid5").title("test1 게시글5").content("test1 다섯 번째 내용").user(user1).build();
        Article article5 = Article.builder().articleId(1005).articleUuid("article_uuid6").title("test2 게시글1").content("test2 첫 번째 내용").user(user2).build();
        Article article6 = Article.builder().articleId(1006).articleUuid("article_uuid7").title("test2 게시글2").content("test2 두 번째 내용").user(user2).build();
        Article article7 = Article.builder().articleId(1007).articleUuid("article_uuid8").title("test2 게시글3").content("test2 세 번째 내용").user(user2).build();
        Article article8 = Article.builder().articleId(1008).articleUuid("article_uuid9").title("test2 게시글4").content("test2 네 번째 내용").user(user2).build();
        Article article9 = Article.builder().articleId(1009).articleUuid("article_uuid10").title("test2 게시글5").content("test2 다섯 번째 내용").user(user2).build();
        Article article10 = Article.builder().articleId(1010).articleUuid("article_uuid11").title("test1 게시글6").content("test1 여섯 번째 내용").user(user1).build();
        Article article11 = Article.builder().articleId(1011).articleUuid("article_uuid12").title("test1 게시글7").content("test1 일곱 번째 내용").user(user1).build();
        Article article12 = Article.builder().articleId(1012).articleUuid("article_uuid13").title("test2 게시글6").content("test2 여섯 번째 내용").user(user2).build();
        Article article13 = Article.builder().articleId(1013).articleUuid("article_uuid14").title("test2 게시글7").content("test2 일곱 번째 내용").user(user2).build();
        Article article14 = Article.builder().articleId(1014).articleUuid("article_uuid15").title("test1 게시글8").content("test1 여덟 번째 내용").user(user1).build();

        articleRepository.addAll(List.of(article0, article1, article2, article3, article4, article5, article6, article7, article8, article9, article10, article11, article12, article13, article14));

        article0.increaseCommentCount();
        article0.increaseCommentCount();
        article1.increaseCommentCount();
        article1.increaseCommentCount();
        article2.increaseCommentCount();
        article3.increaseCommentCount();
        article4.increaseCommentCount();
        article4.increaseCommentCount();
        article5.increaseCommentCount();

        commentRepository.addAll(List.of(
                Comment.builder().commentId(1000).commentUuid("comment_uuid1").content("test1의 첫 번째 댓글입니다.").user(user1).article(article0).build(),
                Comment.builder().commentId(1001).commentUuid("comment_uuid2").content("test2의 첫 번째 댓글입니다.").user(user2).article(article0).build(),
                Comment.builder().commentId(1002).commentUuid("comment_uuid3").content("test1의 첫 번째 댓글입니다.").user(user1).article(article1).build(),
                Comment.builder().commentId(1003).commentUuid("comment_uuid4").content("test2의 첫 번째 댓글입니다.").user(user2).article(article1).build(),
                Comment.builder().commentId(1004).commentUuid("comment_uuid5").content("test1의 첫 번째 댓글입니다.").user(user1).article(article2).build(),
                Comment.builder().commentId(1005).commentUuid("comment_uuid6").content("test1의 첫 번째 댓글입니다.").user(user1).article(article3).build(),
                Comment.builder().commentId(1006).commentUuid("comment_uuid7").content("test1의 첫 번째 댓글입니다.").user(user1).article(article4).build(),
                Comment.builder().commentId(1007).commentUuid("comment_uuid8").content("test2의 첫 번째 댓글입니다.").user(user2).article(article4).build(),
                Comment.builder().commentId(1008).commentUuid("comment_uuid9").content("test2의 첫 번째 댓글입니다.").user(user2).article(article5).build()
        ));
    }
}