package kakaotech.task4.common.config;

import jakarta.annotation.PostConstruct;
import kakaotech.task4.domain.article.entity.Article;
import kakaotech.task4.domain.article.repository.ArticleRepository;
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

        articleRepository.addAll(List.of(
                Article.builder().articleId(1000).articleUuid("article_uuid1").title("test1 게시글1").content("test1 첫 번째 내용").imageUrl("imageUrl1").user(user1).build(),
                Article.builder().articleId(1001).articleUuid("article_uuid2").title("test1 게시글2").content("test1 두 번째 내용").imageUrl("imageUrl2").user(user1).build(),
                Article.builder().articleId(1002).articleUuid("article_uuid3").title("test1 게시글3").content("test1 세 번째 내용").imageUrl("imageUrl3").user(user1).build(),
                Article.builder().articleId(1003).articleUuid("article_uuid4").title("test1 게시글4").content("test1 네 번째 내용").imageUrl("imageUrl4").user(user1).build(),
                Article.builder().articleId(1004).articleUuid("article_uuid5").title("test1 게시글5").content("test1 다섯 번째 내용").imageUrl("imageUrl5").user(user1).build(),
                Article.builder().articleId(1005).articleUuid("article_uuid6").title("test2 게시글1").content("test2 첫 번째 내용").imageUrl("imageUrl6").user(user2).build(),
                Article.builder().articleId(1006).articleUuid("article_uuid7").title("test2 게시글2").content("test2 두 번째 내용").imageUrl("imageUrl7").user(user2).build(),
                Article.builder().articleId(1007).articleUuid("article_uuid8").title("test2 게시글3").content("test2 세 번째 내용").imageUrl("imageUrl8").user(user2).build(),
                Article.builder().articleId(1008).articleUuid("article_uuid9").title("test2 게시글4").content("test2 네 번째 내용").imageUrl("imageUrl9").user(user2).build(),
                Article.builder().articleId(1009).articleUuid("article_uuid10").title("test2 게시글5").content("test2 다섯 번째 내용").imageUrl("imageUrl10").user(user2).build(),
                Article.builder().articleId(1010).articleUuid("article_uuid11").title("test1 게시글6").content("test1 여섯 번째 내용").imageUrl("imageUrl11").user(user1).build(),
                Article.builder().articleId(1011).articleUuid("article_uuid12").title("test1 게시글7").content("test1 일곱 번째 내용").imageUrl("imageUrl12").user(user1).build(),
                Article.builder().articleId(1012).articleUuid("article_uuid13").title("test2 게시글6").content("test2 여섯 번째 내용").imageUrl("imageUrl13").user(user2).build(),
                Article.builder().articleId(1013).articleUuid("article_uuid14").title("test2 게시글7").content("test2 일곱 번째 내용").imageUrl("imageUrl14").user(user2).build(),
                Article.builder().articleId(1014).articleUuid("article_uuid15").title("test1 게시글8").content("test1 여덟 번째 내용").imageUrl("imageUrl15").user(user1).build()
        ));
    }
}