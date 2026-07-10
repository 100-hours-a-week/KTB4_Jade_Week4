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
              "message": "인증되지 않은 사용자입니다."
            }
            """;

    public static final String AUTH_401_002 = """
            {
              "status": "UNAUTHORIZED",
              "code": "AUTH-401-002",
              "message": "이메일 또는 비밀번호가 다릅니다."
            }
            """;

    public static final String AUTH_403_001 = """
            {
              "status": "FORBIDDEN",
              "code": "AUTH-403-001",
              "message": "접근 권한이 없습니다."
            }
            """;

    public static final String AUTH_403_002 = """
            {
              "status": "FORBIDDEN",
              "code": "AUTH-403-002",
              "message": "CSRF Token이 유효하지 않습니다."
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

    public static final String JWT_401_001 = """
            {
              "status": "UNAUTHORIZED",
              "code": "JWT-401-001",
              "message": "Access Token이 없습니다."
            }
            """;

    public static final String JWT_401_002 = """
            {
              "status": "UNAUTHORIZED",
              "code": "JWT-401-002",
              "message": "Access Token이 만료되었습니다."
            }
            """;

    public static final String JWT_401_003 = """
            {
              "status": "UNAUTHORIZED",
              "code": "JWT-401-003",
              "message": "Access Token이 유효하지 않습니다."
            }
            """;

    public static final String JWT_401_004 = """
            {
              "status": "UNAUTHORIZED",
              "code": "JWT-401-004",
              "message": "Refresh Token이 만료되었습니다."
            }
            """;

    public static final String JWT_401_005 = """
            {
              "status": "UNAUTHORIZED",
              "code": "JWT-401-005",
              "message": "Refresh Token이 유효하지 않습니다."
            }
            """;

    public static final String JWT_404_001 = """
            {
              "status": "NOT_FOUND",
              "code": "JWT-404-001",
              "message": "Refresh Token을 찾을 수 없습니다."
            }
            """;

    public static final String JWT_500_001 = """
            {
              "status": "INTERNAL_SERVER_ERROR",
              "code": "JWT-500-001",
              "message": "토큰 해시에 실패했습니다."
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