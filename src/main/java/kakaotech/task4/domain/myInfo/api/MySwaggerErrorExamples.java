package kakaotech.task4.domain.myInfo.api;

public final class MySwaggerErrorExamples {
    private MySwaggerErrorExamples() {}

    public static final String MY_400_001 = """
            {
                "code": "GLOBAL-400-001",
                "status": "BAD_REQUEST",
                "message": "변경할 내용을 입력해주세요.",
                "fields": {
                    "nickname": "닉네임을 입력해주세요.",
                    "profileImageUrl": "프로필 이미지를 입력해주세요."
                }
            }
            """;

    public static final String MY_409_001 = """
            {
                "code": "MY-409-001",
                "status": "CONFLICT",
                "message": "중복된 닉네임입니다."
            }
            """;

    public static final String MY_422_001 = """
            {
                "code": "GLOBAL-422-001",
                "status": "UNPROCESSABLE_ENTITY",
                "message": "띄어쓰기 없이 10글자 이내로 입력해주세요."
            }
            """;
}