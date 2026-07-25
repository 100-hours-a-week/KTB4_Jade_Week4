# 탈퇴 이메일 재사용 — soft delete와 unique 제약의 충돌

대상 코드: `domain/member/entity/Member.java`, `domain/member/repository/MemberRepository.java`, `common/baseEntity/BaseEntity.java`, `domain/auth/service/AuthService.java`

한 줄 요약: 탈퇴는 soft delete인데 `email` 은 DB unique 제약이 걸려 있어, 탈퇴한 회원의 이메일로는 영원히 재가입할 수 없다.

상태: **미해결. 다음 과제로 보류** (2026-07-25 기준)

---

## 1. 현재 구조

`softDelete()` 는 `deletedAt` 에 시각만 찍는다. 행은 남고 email도 그대로 남는다.

```java
// BaseEntity
public void softDelete() {
    this.deletedAt = LocalDateTime.now();
}
```

```java
// Member
@Column(nullable = false, unique = true)
private String email;
```

조회 계열 쿼리는 `deletedAt is null` 로 탈퇴 회원을 걸러낸다.

```java
@Query("""
        select m from Member m
        where m.email = :email
          and m.deletedAt is null
        """)
Optional<Member> findByEmail(@Param("email") String email);
```

그래서 탈퇴 회원은 로그인도 안 되고 조회에도 안 잡힌다. 여기까진 의도대로다.

---

## 2. 문제

중복 검사만 필터가 없다.

```java
boolean existsByEmail(String email);   // deletedAt 무시. 탈퇴 회원도 센다
```

`AuthService` 회원가입에서 이 값을 쓴다.

```
a@x.com 가입 → 탈퇴 → a@x.com 재가입 시도
  → existsByEmail("a@x.com") = true  (탈퇴 행이 잡힘)
  → DUPLICATE_EMAIL 예외
```

이메일이 영구히 묶인다. 실서비스 관점에서는 버그다.

---

## 3. 왜 그냥 필터를 못 넣는가

닉네임 쪽은 이미 이 방식으로 고쳤다.

```java
@Query("""
        select count(m) > 0 from Member m
        where m.nickname = :nickname
          and m.deletedAt is null
        """)
boolean existsByNickname(@Param("nickname") String nickname);
```

닉네임은 **DB에 unique 제약이 없어서** 이걸로 끝난다. 애플리케이션 레벨 검사가 유일한 방어선이니, 그 검사만 고치면 동작이 바뀐다.

이메일은 다르다. `unique = true` 가 DB에 실제로 걸려 있다.

```
existsByEmail 에 deletedAt is null 추가
  → 애플리케이션 검사 통과 (탈퇴 행 안 보임)
  → INSERT 실행
  → DB unique 제약 위반. 탈퇴 행이 여전히 같은 email 점유 중
  → DataIntegrityViolationException → 500
```

**지금은 400 DUPLICATE_EMAIL, 고치면 500.** 더 나빠진다. 그래서 손대지 않았다.

DB 제약을 같이 바꾸지 않는 한 애플리케이션 쿼리만 고치는 건 의미가 없다.

---

## 4. 방법 A — 탈퇴 시 email 마스킹

탈퇴 순간 email 값 자체를 바꿔 자리를 비운다.

```java
public void softDelete() {
    super.softDelete();
    this.email = "deleted_" + this.memberId + "_" + this.email;
}
```

```
탈퇴 전:  a@x.com
탈퇴 후:  deleted_17_a@x.com     ← unique 자리 비움
재가입:   a@x.com                ← 통과
```

`memberId` 를 섞는 이유: 같은 이메일로 가입-탈퇴를 반복해도 마스킹 값끼리 충돌하지 않게 하려고. `deleted_a@x.com` 만 쓰면 두 번째 탈퇴에서 또 막힌다.

`existsByEmail` 은 그대로 둬도 된다. 마스킹된 값은 어차피 검색어와 다르니 안 잡힌다. 다만 명시적으로 `deletedAt is null` 을 넣어두는 편이 의도가 드러난다.

- 장점: 컬럼 추가 없음. 마이그레이션 없음. 코드 3줄
- 단점: 원본 email이 훼손된다. 탈퇴 철회/계정 복구 기능을 나중에 넣으려면 파싱해서 되돌려야 한다
- `length = 512` 등 컬럼 길이 여유 확인 필요 (현재 email에 length 지정 없음 → 기본 255)

---

## 5. 방법 B — 복합 unique

email 단독 unique를 버리고 `(email, 삭제여부)` 쌍을 unique로 만든다.

```
email      deletedAt
a@x.com    2026-01-10   ← 탈퇴한 옛 계정
a@x.com    NULL         ← 새로 가입한 계정
```

쌍이 다르므로 공존 가능.

### 함정: NULL

**이대로 하면 뚫린다.** H2/MySQL 모두 unique 인덱스에서 NULL을 서로 다른 값으로 취급한다. `NULL != NULL` 이라서 중복 판정이 안 된다.

```
a@x.com    NULL
a@x.com    NULL   ← 막히지 않음
```

활성 계정 중복 — 정작 막아야 할 케이스 — 이 통과한다. 애플리케이션 검사가 유일한 방어선으로 되돌아가므로 동시 요청 시 중복 가입이 가능해진다.

### 해결: 센티널 값

NULL 대신 "활성"을 뜻하는 고정 값을 쓴다. `deletedAt` 은 의미상 NULL이어야 하니 별도 컬럼을 둔다.

```java
@Column(nullable = false)
private LocalDateTime deletedFlag = LocalDateTime.of(1970, 1, 1);   // 활성

public void softDelete() {
    this.deletedAt = LocalDateTime.now();
    this.deletedFlag = this.deletedAt;
}
```

```java
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "deletedFlag"}))
```

활성 계정은 전부 `1970-01-01` 이라 email 중복이 DB에서 차단된다. 탈퇴 계정은 탈퇴 시각이 제각각이라 여러 개 공존한다.

- 장점: email 원본 보존. 계정 복구 가능. DB 레벨 방어 유지
- 단점: 컬럼 추가 + 유니크 재정의 + 기존 데이터 백필. `ddl-auto: create` 인 지금은 무비용이지만 운영 DB 붙으면 마이그레이션 필요
- 같은 시각(밀리초 단위)에 같은 이메일 두 건이 탈퇴하면 이론상 충돌. 현실적으로 무시 가능하나 엄밀히는 `memberId` 를 센티널에 섞는 편이 안전

---

## 6. 판단

개인 프로젝트 규모면 **A안 권장**. 코드 3줄이고 마이그레이션이 없다.

계정 복구나 탈퇴 철회를 스펙에 넣을 계획이면 B안. 원본 email이 살아있어야 한다.

---

## 7. 함께 볼 것

- 닉네임 쪽 동일 문제는 이미 수정됨 (`existsByNickname` 에 `deletedAt is null` 추가)
- `MyInfoService.validateDuplicateNickname` — 프로필만 변경할 때 본인 닉네임을 중복으로 판정하던 버그도 함께 수정됨. 본인 현재 닉네임이거나 `null` 이면 검사 스킵
- 탈퇴 회원의 게시글/댓글이 어떻게 처리되는지는 별도 확인 필요. 이 문서 범위 밖
