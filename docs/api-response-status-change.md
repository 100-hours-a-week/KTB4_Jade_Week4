# `ApiResponse` 에 `status` 추가 + `toEntity()` 도입 — 발단과 결과

관련 문서: [jackson-record-serialization.md](./jackson-record-serialization.md)
대상 코드: `common/response/ApiResponse.java`, 컨트롤러 6개

---

## 1. 배경 — 왜 손댔나

기존 응답 바디에는 HTTP 상태가 전혀 없었다. 상태는 `ResponseEntity.status(...)` 로 **HTTP 상태 라인에만** 나갔다.

```
HTTP/1.1 404
{"success":false,"message":"해당 게시글을 찾을 수 없습니다.","code":"ARTICLE-404-001"}
```

숫자 `404` 만 있고 `NOT_FOUND` 라는 이름은 어디에도 없었다.
스웨거 예시에만 `"status": "NOT_FOUND"` 가 적혀 있었는데, 실제 응답에 없는 필드였다 (예시가 실제와 불일치).

목표: 상태 코드 의미를 바디에 명시적으로 노출.

---

## 2. Jackson 검증이 필요해진 발단

옵션 B(`toEntity()`) 적용 때 **`ApiResponse` record에 인스턴스 메서드를 처음 추가**한 것이 발단.

세션 전 record 본문은 전부 `static` 이었다.

```java
public static <T> ApiResponse<T> success(T data)                    { ... }
public static <T> ApiResponse<T> success(String message, T data)    { ... }
public static ApiResponse<Void> error(ExceptionCode code)           { ... }
public static ApiResponse<Void> error(ExceptionCode code, Map<...>) { ... }
```

Jackson은 static 메서드를 프로퍼티 후보로 보지 않는다 → "이게 JSON 키로 새나?"라는 질문 자체가 생길 여지가 없었다.

추가된 메서드 (`ApiResponse.java:36`):

```java
public ResponseEntity<ApiResponse<T>> toEntity() {
    return ResponseEntity.status(status).body(this);
}
```

**인스턴스** 메서드 + 리턴값이 자기 자신을 품은 `ResponseEntity`.
이 두 조건이 겹쳐서 이름 검증이 필요해졌다. 상세는 관련 문서 참고.

---

## 3. 바뀐 결과 — 두 갈래로 분리

### (A) `toEntity()` 때문에 JSON이 바뀐 것: 없음

```json
{"status":"OK","success":true,"data":{"a":1}}
```

`entity` 키 안 생김 → `@JsonIgnore` 미부착.

이번 검증의 산출물은 코드 변경이 아니라 **이름 선택 근거**다.
`getEntity()` 로 지었다면 `getBody()` 가 자기 자신을 되짚어 무한 재귀 → 전 엔드포인트 500.

### (B) `status` 컴포넌트 때문에 바뀐 것: JSON 키 하나 추가

Jackson 규칙과 무관한, 의도한 스펙 변경.

```diff
-{"success":false,"message":"해당 게시글을 찾을 수 없습니다.","code":"ARTICLE-404-001"}
+{"status":"NOT_FOUND","success":false,"message":"해당 게시글을 찾을 수 없습니다.","code":"ARTICLE-404-001"}
```

성공 응답도 상태별로 다르게 나간다.

```json
{"status":"OK","success":true,"data":{ ... }}
{"status":"CREATED","success":true,"message":"회원가입이 완료되었습니다."}
```

`HttpStatus` 는 enum이라 숫자가 아니라 이름으로 직렬화된다.

---

## 4. 바뀐 코드

### `ApiResponse`

```diff
 @JsonInclude(JsonInclude.Include.NON_NULL)
 public record ApiResponse<T>(
+        HttpStatus status,
         boolean success,
         T data,
         String message,
         String code,
         Map<String, Object> fields
 ) {
+    public static <T> ApiResponse<T> created(T data) { ... }
+    public static <T> ApiResponse<T> created(String message, T data) { ... }
+
+    public ResponseEntity<ApiResponse<T>> toEntity() {
+        return ResponseEntity.status(status).body(this);
+    }
+
     public static ApiResponse<Void> error(ExceptionCode exceptionCode) {
-        return new ApiResponse<>(false, null, exceptionCode.getMessage(), exceptionCode.getCode(), null);
+        return error(exceptionCode, null);
     }
```

`error(...)` 는 `exceptionCode.getStatus()` 로 status를 **자동** 채운다.
덕분에 예외 처리 쪽은 수정 0줄:

- `GlobalExceptionHandler`
- `JwtAuthenticationEntryPoint`
- `SecurityAccessDeniedHandler`
- `JwtExceptionFilter`

### 컨트롤러 12곳

```diff
-return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
+return ApiResponse.success(response).toEntity();
```

```diff
-return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
+return ApiResponse.created(response).toEntity();
```

| 컨트롤러 | 변경 메서드 |
|---|---|
| `ArticleController` | createArticle(201), getArticleList, getArticleDetail |
| `ArticleLikeController` | like, unlike |
| `ArticleVoteController` | vote |
| `CommentController` | createComment(201) |
| `AuthController` | signUp(201), signIn, reissue |
| `MyInfoController` | getMyBasicInfo, updateMyBasicInfo |

`signUp` 만 data가 null이라 타입 명시가 필요하다.

```java
return ApiResponse.<Void>created(AuthSuccessCode.SIGN_UP_SUCCESS.getMessage(), null).toEntity();
```

`HttpStatus` import 제거: `ArticleLikeController`, `ArticleVoteController` (204 응답이 없어 더는 안 씀).
나머지 4개 컨트롤러는 204 반환 때문에 유지.

---

## 5. 효과

- HTTP 상태 라인과 바디 `status` 가 **팩토리 한 곳**에서 나옴 → 구조적으로 어긋날 수 없음
- 변경 전에는 두 곳(`ResponseEntity.status(...)` / `ApiResponse.success(...)`)이 따로 상태를 말해서, 한쪽만 바꾸면 바디가 조용히 거짓말할 수 있었음
- 비즈니스 로직(서비스·엔티티·리포지토리) 무수정
- `./gradlew test` BUILD SUCCESSFUL

---

## 6. 남은 항목

**204 응답은 바디가 없어 `status` 가 안 보인다.**
해당 엔드포인트: 게시글 수정/삭제, 댓글 수정/삭제, 비밀번호 변경, 회원 탈퇴, 로그아웃, `GET /auth/csrf`.

노출하려면 `200 + ApiResponse.success(null)` 로 바꿔야 한다. 미결정 상태이며, 스웨거는 현재 구현대로 204 empty content로 문서화되어 있다.
