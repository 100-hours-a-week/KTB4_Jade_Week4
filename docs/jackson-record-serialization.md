# Jackson이 record를 직렬화하는 규칙 — `toEntity()` 는 왜 응답 JSON에 안 섞이나

대상 코드: `common/response/ApiResponse.java`
확인 환경: Jackson 2.18.3 (Spring Boot 관리 버전), 이 프로젝트에서 직접 실행 측정

---

## 1. Jackson은 "필드를 덤프"하지 않는다

직관적으로는 "객체 안에 든 걸 다 뱉는다"고 생각하기 쉽지만 틀렸다.
Jackson은 클래스를 먼저 뜯어서 **프로퍼티 목록**을 만들고, **그 목록만** JSON 키로 쓴다.
목록에 못 들어간 것은 객체에 존재해도 JSON에 안 나온다.

일반 클래스에서 목록에 들어가는 기준은 **이름 규칙**(자바빈 관례)이다.

| 메서드 | JSON 키 | 규칙 |
|---|---|---|
| `getName()` | `name` | `get` 떼고 앞글자 소문자 |
| `isActive()` (boolean) | `active` | `is` 떼고 앞글자 소문자 |
| 그 외 이름 | 없음 | 무시 |

판정 기준은 **접근 제어자가 아니라 이름**이다. `public` 이어도 이름이 규칙에 안 맞으면 안 나온다.

---

## 2. record는 왜 특별 취급이 필요했나

record가 자동 생성하는 접근자에는 `get` 접두사가 없다.

```java
public record ApiResponse<T>(
        HttpStatus status,
        boolean success,
        T data,
        String message,
        String code,
        Map<String, Object> fields
) { }
// 자동 생성 접근자: status(), success(), data(), message(), code(), fields()
```

`status()` 는 위의 게터 규칙에 안 걸린다. 규칙만 적용하면 record는 `{}` 로 직렬화된다.
그래서 Jackson은 2.12부터 record 전용 처리를 넣었다.

> **record 컴포넌트 이름과 같은 무인자 메서드는 프로퍼티로 등록한다.**

우리 응답 JSON 키가 `status`, `success`, `data`, `message`, `code`, `fields` 6개인 이유가 이것이다. record 컴포넌트가 6개니까.

---

## 3. `toEntity()` 는 어느 규칙에도 안 걸린다

```java
public ResponseEntity<ApiResponse<T>> toEntity() {
    return ResponseEntity.status(status).body(this);
}
```

- record 컴포넌트 이름인가? ❌ 컴포넌트에 `toEntity` 없음
- `get` / `is` 접두사인가? ❌

→ 프로퍼티 목록 진입 실패 → JSON에 안 나온다.

---

## 4. `@JsonIgnore` 가 불필요한 이유

`@JsonIgnore` 의 뜻은 "**프로퍼티로 잡힌 것을** 목록에서 제외하라"다.
애초에 프로퍼티로 잡히지 않는 대상에 붙이면 하는 일이 없다. 그래서 생략했다.

(붙여도 무해하다. "이 메서드는 응답에 안 나간다"는 의도를 코드에 남기고 싶다면 붙여도 된다.)

---

## 5. 이걸 왜 확인했나 — 안 걸렸다면 터진다

만약 `toEntity()` 가 프로퍼티로 잡혔다면:

```
ApiResponse 직렬화
  → entity 키 → ResponseEntity 직렬화
      → getBody() == 그 ApiResponse (자기 자신)
          → 다시 entity 키 → ... 무한
```

결과: `JsonMappingException: Infinite recursion (StackOverflowError)`.
응답을 만들 때마다 500이 난다. 그래서 응답 record에 메서드를 추가할 때는 이름 확인이 필수다.

---

## 6. 실측 결과

probe record를 만들어 실제로 직렬화해봤다.

```java
record Probe(String a) {
    public String toEntity() { return "toEntity값"; }
    public String getExtra() { return "getExtra값"; }
    public boolean isFlag()  { return true; }
    public String plain()    { return "plain값"; }
}
```

출력:

```json
{"a":"x","extra":"getExtra값","flag":true}
```

| 메서드 | JSON 포함 | 이유 |
|---|---|---|
| `a()` | ✅ `a` | record 컴포넌트 |
| `getExtra()` | ✅ `extra` | 게터 이름 규칙 |
| `isFlag()` | ✅ `flag` | boolean 게터 이름 규칙 |
| `toEntity()` | ❌ | 컴포넌트도 게터도 아님 |
| `plain()` | ❌ | 컴포넌트도 게터도 아님 |

실제 `ApiResponse` 출력도 확인했다.

```json
{"status":"OK","success":true,"data":{"a":1}}
{"status":"CREATED","success":true,"message":"생성됨"}
{"status":"NOT_FOUND","success":false,"message":"존재하지 않는 페이지입니다.","code":"GLOBAL-404-001","fields":{"x":"y"}}
```

`entity` 키 없음. `HttpStatus` 는 enum이라 숫자가 아니라 이름(`OK`, `CREATED`, `NOT_FOUND`)으로 직렬화된다.

---

## 7. 정정

이전에 "Jackson은 record 컴포넌트만 직렬화한다"고 말했는데 부정확하다.
정확히는 **record 컴포넌트 + 게터 이름 규칙 둘 다** 잡는다.
`getEntity()` 로 이름 지었다면 잡혀서 무한 재귀로 터졌을 것이고, `toEntity()` 라서 안전한 것이다.

---

## 8. 부가 사실

- record의 `private final` 필드는 자동 수집되지 않는다. Jackson 기본 필드 가시성이 public-only이기 때문. 그래서 `status` 가 두 번 나오지 않는다.
- `@JsonInclude(NON_NULL)` 은 컴포넌트에 그대로 적용된다 → `data` / `message` / `code` / `fields` 가 null이면 키 자체가 생략된다.
- `success` 는 primitive `boolean` 이라 null이 될 수 없어 항상 나온다.
- 위 측정은 맨 `new ObjectMapper()` 로 했다. Spring이 만드는 매퍼도 이 부분 규칙은 같다 (`application.yml` 에 가시성·네이밍 설정 없음, `jackson.time-zone` 과 `serialization.write-dates-as-timestamps: false` 만 있음).

---

## 9. 실무 규칙

응답 DTO(record든 일반 클래스든)에 편의 메서드를 추가할 때:

1. `get` / `is` 로 시작하지 말 것
2. record 컴포넌트 이름과 겹치지 말 것
3. 권장 접두사: `toXxx`, `asXxx`, `withXxx`
4. 굳이 `getXxx()` 여야 한다면 `@JsonIgnore` 필수
