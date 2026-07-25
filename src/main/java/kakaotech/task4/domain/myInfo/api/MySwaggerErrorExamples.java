package kakaotech.task4.domain.myInfo.api;

public final class MySwaggerErrorExamples {
    private MySwaggerErrorExamples() {}

    public static final String MY_400_001 = """
            {
                "status": "BAD_REQUEST",
                "success": false,
                "message": "변경할 내용을 입력해주세요.",
                "code": "MY-400-001"
            }
            """;

    public static final String GLOBAL_400_001 = """
        {
            "status": "BAD_REQUEST",
            "success": false,
            "message": "필수 값이 제외되었습니다.",
            "code": "GLOBAL-400-001",
            "fields": {
                "nowPassword": "비밀번호를 입력해주세요.",
                "nextPassword": "비밀번호를 입력해주세요.",
                "checkNextPassword": "비밀번호 확인을 입력해주세요."
            }
        }
        """;

    public static final String MY_409_001 = """
            {
                "status": "CONFLICT",
                "success": false,
                "message": "중복된 닉네임입니다.",
                "code": "MY-409-001"
            }
            """;

    public static final String GLOBAL_422_001_NICKNAME = """
            {
                "status": "UNPROCESSABLE_ENTITY",
                "success": false,
                "message": "필드의 유효성 검사가 올바르지 않습니다.",
                "code": "GLOBAL-422-001",
                "fields": {
                    "nickname": "띄어쓰기 없이 10글자 이내로 입력해주세요."
                }
            }
            """;

    public static final String GLOBAL_422_001_PASSWORD = """
        {
            "status": "UNPROCESSABLE_ENTITY",
            "success": false,
            "message": "필드의 유효성 검사가 올바르지 않습니다.",
            "code": "GLOBAL-422-001",
            "fields": {
                "nowPassword": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.",
                "nextPassword": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
            }
        }
        """;

    public static final String MY_422_001_NOW_PASSWORD = """
        {
            "status": "UNPROCESSABLE_ENTITY",
            "success": false,
            "message": "필드의 유효성 검사가 올바르지 않습니다.",
            "code": "MY-422-001",
            "fields": {
                "nowPassword": "현재 비밀번호가 일치하지 않습니다."
            }
        }
        """;

    public static final String MY_422_001_PASSWORD_MISMATCH = """
        {
            "status": "UNPROCESSABLE_ENTITY",
            "success": false,
            "message": "필드의 유효성 검사가 올바르지 않습니다.",
            "code": "MY-422-001",
            "fields": {
                "checkNextPassword": "비밀번호가 다릅니다."
            }
        }
        """;
}
