package kakaotech.task4.domain.articleVote.api;

public final class ArticleVoteSwaggerSuccessExamples {
    private ArticleVoteSwaggerSuccessExamples() {}

    public static final String VOTE_200_001 = """
            {
                "status": "OK",
                "success": true,
                "data": {
                    "voteCountA": 483,
                    "voteCountB": 732,
                    "myVote": "A",
                    "changed": true,
                    "wasFirst": true
                }
            }
            """;

    public static final String VOTE_200_002 = """
            {
                "status": "OK",
                "success": true,
                "data": {
                    "voteCountA": 482,
                    "voteCountB": 733,
                    "myVote": "B",
                    "changed": true,
                    "wasFirst": false
                }
            }
            """;

    public static final String VOTE_200_003 = """
            {
                "status": "OK",
                "success": true,
                "data": {
                    "voteCountA": 482,
                    "voteCountB": 732,
                    "myVote": "B",
                    "changed": false,
                    "wasFirst": false
                }
            }
            """;
}
