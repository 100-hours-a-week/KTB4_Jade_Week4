## 작업 내용

- 응답 구조 통일 ( `SuccessRes` / `ExceptionRes` → `ApiResponse<T>` )
- JWT 인증 principal `Member` → `memberUuid` 변경 및 보안 설정 정리
- 비밀번호 변경 시 현재 비밀번호 검증 로직 추가
- 인증/인가 로직 단위 테스트 추가

## 주요 변경사항

- `ApiResponse` : 성공/에러 응답을 하나의 타입으로 통합, `@JsonInclude(NON_NULL)`로 불필요 필드 제거
- `GlobalExceptionHandler` : `ExceptionRes` 제거하고 `ApiResponse` 사용, 검증 에러 코드 분기(`resolveValidationErrorCode`)와 예외 응답 생성 로직의 삼항 연산자 분기 제거
- `JwtAuthService` : 인증 성공 시 `Authentication` principal을 `Member` 엔티티 대신 `memberUuid` 문자열로 저장
- `CurrentMemberArgumentResolver` : principal 타입 변경에 맞춰 `memberUuid`로 `MemberService` 조회 후 `Member` 반환
- `MyInfoService` : `updateMySecurity`에 `validateNowPassword` 추가해 현재 비밀번호 검증 후 비밀번호 변경
- `UpdateMySecurityRequest` : `password/checkPassword` → `nowPassword/nextPassword/checkNextPassword`로 필드 분리
- `CommonFieldError` : `INVALID_NOW_PASSWORD` 필드 에러 추가, `PASSWORD_MISMATCH` 필드명 `checkNextPassword`로 수정
- `CorsConfig` 제거, `SecurityConfig` / `SecurityPaths` 정리

## 예외 처리

- 401 (Unauthorized) : principal이 문자열(memberUuid)이 아니거나 인증 정보 없음
- 400 (Invalid Password) : 현재 비밀번호 불일치 / 새 비밀번호-확인 불일치
