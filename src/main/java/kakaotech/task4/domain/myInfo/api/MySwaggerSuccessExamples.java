package kakaotech.task4.domain.myInfo.api;

public final class MySwaggerSuccessExamples {
    private MySwaggerSuccessExamples() {}

    public static final String MY_200_001 = """
            {
                "status": "OK",
                "success": true,
                "data": {
                    "email": "jade@kakaotech.com",
                    "nickname": "jade",
                    "profileImageUrl": "url"
                }
            }
            """;

    public static final String MY_200_002 = """
        {
            "status": "OK",
            "success": true,
            "data": {
                "profileImageUrl": "프로필이미지url",
                "nickname": "jade"
            }
        }
        """;
}
