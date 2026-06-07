package kakaotech.task4.auth.api;

public final class AuthSwaggerErrorExamples {
    private AuthSwaggerErrorExamples() {}

    public static final String SIGN_UP_400 = """
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

    public static final String SIGN_UP_409 = """
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

    public static final String SIGN_UP_422 = """
            {
              "status": "UNPROCESSABLE_ENTITY",
              "code": "AUTH-422-001",
              "message": "필드의 유효성 검사가 올바르지 않습니다.",
              "fields": {
                "email": "올바른 이메일 주소 형식을 입력해주세요.",
                "password": "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.",
                "checkPassword": "비밀번호가 다릅니다.",
                "nickname": "띄어쓰기 없이 10글자 이내로 입력해주세요.",
                "profileImageUrl": "프로필 사진을 추가해주세요."
              }
            }
            """;


}