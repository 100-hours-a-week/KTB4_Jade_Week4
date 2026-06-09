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

    public static final String MY_400_002 = """
        {
            "code": "GLOBAL-400-001",
            "status": "BAD_REQUEST",
            "message": "필수 값이 제외되었습니다.",
            "fields": {
                "password": "비밀번호를 입력해주세요.",
                "checkPassword": "비밀번호 확인을 입력해주세요."
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

    public static final String MY_422_002 = """
        {
            "status": "UNPROCESSABLE_ENTITY",
            "code": "GLOBAL-422-002",
            "message": "필드의 유효성 검사가 올바르지 않습니다.",
            "fields": {
                "password": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.",
                "checkPassword": "비밀번호가 다릅니다."
            }
        }
        """;
}