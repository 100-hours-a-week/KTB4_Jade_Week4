package kakaotech.task4.articleVote;

import kakaotech.task4.domain.articleVote.entity.ArticleVoteCount;
import kakaotech.task4.domain.articleVote.repository.ArticleVoteCountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.show-sql=true")
@ExtendWith(OutputCaptureExtension.class)
class ArticleVoteCountPersistenceTest {
    private static final String SQL_CAPTURE_START = "sql-capture-start";

    @Autowired
    private ArticleVoteCountRepository articleVoteCountRepository;

    @Test
    void newAssignedIdEntityIsPersistedWithoutSelect(CapturedOutput output) {
        System.out.println(SQL_CAPTURE_START);

        articleVoteCountRepository.saveAndFlush(ArticleVoteCount.of(999_999L));

        String outputAfterMarker = output.getOut()
                .substring(output.getOut().lastIndexOf(SQL_CAPTURE_START))
                .toLowerCase();

        assertThat(outputAfterMarker)
                .contains("insert", "article_vote_count")
                .doesNotContain("select");
    }
}
