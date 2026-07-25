package kakaotech.task4.domain.auth.api;

public final class AuthSwaggerErrorExamples {
    private AuthSwaggerErrorExamples() {}

    public static final String GLOBAL_400_001_SIGN_UP = """
            {
              "status": "BAD_REQUEST",
              "success": false,
              "message": "필수 값이 제외되었습니다.",
              "code": "GLOBAL-400-001",
              "fields": {
                "email": "이메일을 입력해주세요.",
                "password": "비밀번호를 입력해주세요.",
                "checkPassword": "비밀번호를 입력해주세요.",
                "nickname": "닉네임을 입력해주세요.",
                "profileImageUrl": "프로필 사진을 추가해주세요."
              }
            }
            """;

    public static final String GLOBAL_400_001_SIGN_IN = """
            {
              "status": "BAD_REQUEST",
              "success": false,
              "message": "필수 값이 제외되었습니다.",
              "code": "GLOBAL-400-001",
              "fields": {
                "email": "이메일을 입력해주세요.",
                "password": "비밀번호를 입력해주세요."
              }
            }
            """;

    public static final String GLOBAL_400_002 = """
            {
              "status": "BAD_REQUEST",
              "success": false,
              "message": "요청 본문의 형식이 올바르지 않습니다.",
              "code": "GLOBAL-400-002"
            }
            """;

    public static final String AUTH_401_001 = """
            {
              "status": "UNAUTHORIZED",
              "success": false,
              "message": "인증되지 않은 사용자입니다.",
              "code": "AUTH-401-001"
            }
            """;

    public static final String AUTH_401_002 = """
            {
              "status": "UNAUTHORIZED",
              "success": false,
              "message": "이메일 또는 비밀번호가 다릅니다.",
              "code": "AUTH-401-002"
            }
            """;

    public static final String AUTH_403_001 = """
            {
              "status": "FORBIDDEN",
              "success": false,
              "message": "접근 권한이 없습니다.",
              "code": "AUTH-403-001"
            }
            """;

    public static final String AUTH_403_002 = """
            {
              "status": "FORBIDDEN",
              "success": false,
              "message": "CSRF Token이 유효하지 않습니다.",
              "code": "AUTH-403-002"
            }
            """;

    public static final String AUTH_409_001 = """
            {
              "status": "CONFLICT",
              "success": false,
              "message": "중복된 데이터입니다.",
              "code": "AUTH-409-001",
              "fields": {
                "email": "중복된 이메일입니다.",
                "nickname": "중복된 닉네임입니다."
              }
            }
            """;

    public static final String AUTH_422_001 = """
            {
              "status": "UNPROCESSABLE_ENTITY",
              "success": false,
              "message": "필드의 유효성 검사가 올바르지 않습니다.",
              "code": "AUTH-422-001",
              "fields": {
                "checkPassword": "비밀번호가 다릅니다."
              }
            }
            """;

    public static final String GLOBAL_422_001_SIGN_UP = """
            {
              "status": "UNPROCESSABLE_ENTITY",
              "success": false,
              "message": "필드의 유효성 검사가 올바르지 않습니다.",
              "code": "GLOBAL-422-001",
              "fields": {
                "email": "올바른 이메일 주소 형식을 입력해주세요.",
                "password": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.",
                "nickname": "띄어쓰기 없이 10글자 이내로 입력해주세요."
              }
            }
            """;

    public static final String GLOBAL_422_001_SIGN_IN = """
            {
              "status": "UNPROCESSABLE_ENTITY",
              "success": false,
              "message": "필드의 유효성 검사가 올바르지 않습니다.",
              "code": "GLOBAL-422-001",
              "fields": {
                "email": "올바른 이메일 주소 형식을 입력해주세요.",
                "password": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
              }
            }
            """;

    public static final String JWT_401_001 = """
            {
              "status": "UNAUTHORIZED",
              "success": false,
              "message": "Access Token이 없습니다.",
              "code": "JWT-401-001"
            }
            """;

    public static final String JWT_401_002 = """
            {
              "status": "UNAUTHORIZED",
              "success": false,
              "message": "Access Token이 만료되었습니다.",
              "code": "JWT-401-002"
            }
            """;

    public static final String JWT_401_003 = """
            {
              "status": "UNAUTHORIZED",
              "success": false,
              "message": "Access Token이 유효하지 않습니다.",
              "code": "JWT-401-003"
            }
            """;

    public static final String JWT_401_004 = """
            {
              "status": "UNAUTHORIZED",
              "success": false,
              "message": "Refresh Token이 만료되었습니다.",
              "code": "JWT-401-004"
            }
            """;

    public static final String JWT_401_005 = """
            {
              "status": "UNAUTHORIZED",
              "success": false,
              "message": "Refresh Token이 유효하지 않습니다.",
              "code": "JWT-401-005"
            }
            """;

    public static final String JWT_404_001 = """
            {
              "status": "NOT_FOUND",
              "success": false,
              "message": "Refresh Token을 찾을 수 없습니다.",
              "code": "JWT-404-001"
            }
            """;
}
