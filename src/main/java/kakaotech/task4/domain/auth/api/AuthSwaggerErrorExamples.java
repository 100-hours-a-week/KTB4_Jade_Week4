package kakaotech.task4.domain.auth.api;

public final class AuthSwaggerErrorExamples {
    private AuthSwaggerErrorExamples() {}

    public static final String AUTH_400_001 = """
            {
              "status": "BAD_REQUEST",
              "code": "AUTH-400-001",
              "message": "필수 값이 제외되었습니다.",
              "fields": {
                "email": "이메일을 입력해주세요.",
                "nickname": "닉네임을 입력해주세요.",
                "password": "비밀번호를 입력해주세요."
              }
            }
            """;

    public static final String AUTH_400_002 = """
        {
            "status": "BAD_REQUEST",
            "code": "AUTH-400-001",
            "message": "필수 값이 제외되었습니다.",
            "fields": {
                "email": "이메일을 입력해주세요.",
                "password": "비밀번호를 입력해주세요."
            }
        }
        """;

    public static final String AUTH_401_001 = """
        {
            "status": "UNAUTHORIZED",
            "code": "AUTH-401-001",
            "message": "로그인 후 사용할 수 있습니다."
        }
        """;

    public static final String AUTH_401_002 = """
        {
            "status": "UNAUTHORIZED",
            "code": "AUTH-401-001",
            "message": "이메일 또는 비밀번호가 다릅니다."
        }
        """;

    public static final String AUTH_409_001 = """
            {
              "status": "CONFLICT",
              "code": "AUTH-409-001",
              "message": "중복된 데이터입니다.",
              "fields": {
                "email": "중복된 이메일입니다.",
                "nickname": "중복된 닉네임입니다."
              }
            }
            """;

    public static final String AUTH_422_001 = """
        {
            "code": "AUTH-422-001",
            "status": "UNPROCESSABLE_ENTITY",
            "message": "필드의 유효성 검사가 올바르지 않습니다.",
            "fields": {
                "checkPassword": "비밀번호가 다릅니다."
            }
        }
        """;

    public static final String AUTH_422_002 = """
        {
            "status": "UNPROCESSABLE_ENTITY",
            "code": "AUTH-422-002",
            "message": "필드의 유효성 검사가 올바르지 않습니다.",
            "fields": {
                "email": "올바른 이메일 주소 형식을 입력해주세요.",
                "password": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
            }
        }
        """;

    public static final String GLOBAL_422_001 = """
        {
            "code": "GLOBAL-422-001",
            "status": "UNPROCESSABLE_ENTITY",
            "message": "필드의 유효성 검사가 올바르지 않습니다.",
            "fields": {
                "nickname": "띄어쓰기 없이 10글자 이내로 입력해주세요.",
                "email": "올바른 이메일 주소 형식을 입력해주세요.",
                "password": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
            }
        }
        """;
}