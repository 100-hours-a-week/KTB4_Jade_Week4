# 좋아요 카운트 버그 — 벌크 UPDATE, 영속성 컨텍스트, 스칼라 조회

대상 코드: `domain/article/repository/ArticleRepository.java`, `domain/article/service/ArticleService.java`, `domain/articleLike/service/ArticleLikeService.java`

한 줄 요약: `@Modifying` 벌크 UPDATE의 `int` 리턴값은 **갱신된 행 수**이지 새 카운트 값이 아닌데, 그 값을 응답의 `likeCount` 로 쓰고 있다.

---

## 1. JPA가 원래 동작하는 방식 — 영속성 컨텍스트

JPA로 엔티티를 조회하면, 그 객체는 **영속성 컨텍스트**(트랜잭션 동안 유지되는 메모리 저장소, 1차 캐시)에 보관된다.

```java
Article article = articleRepository.findByArticleUuid(uuid).get();   // DB SELECT → 메모리에 보관
article.setTitle("바뀐 제목");                                        // 자바 객체 필드만 바꿈
// 트랜잭션 끝 → JPA가 "title 바뀌었네" 감지 → UPDATE SQL 자동 생성 → DB 반영
```

마지막 줄이 핵심이다. `save()` 를 안 불러도 JPA가 **변경 감지(dirty checking)** 로 알아서 UPDATE를 만든다. 이게 JPA의 기본 수정 방식이다.

이 프로젝트의 `article.softDelete()`, `article.update(request)`, `member.updatePassword(...)` 가 전부 이 방식이다.

---

## 2. 그 방식으로 카운트를 올리면 생기는 문제

```java
article.setLikedCount(article.getLikedCount() + 1);
```

이 한 줄이 실제로 하는 일:

1. 메모리에 있는 값을 **읽는다** (129)
2. 자바에서 **더한다** (130)
3. 트랜잭션 끝에 `update article set liked_count = 130 where article_id = 7` 을 **쓴다**

읽기-계산-쓰기가 3단계로 쪼개져 있다. 두 사람이 동시에 좋아요를 누르면:

```
시각  요청A                          요청B                        DB의 liked_count
 1    SELECT → 129 읽음                                           129
 2                                   SELECT → 129 읽음            129
 3    자바에서 129+1=130 계산                                      129
 4                                   자바에서 129+1=130 계산       129
 5    UPDATE ... = 130                                            130
 6                                   UPDATE ... = 130             130   ← A의 +1이 사라짐
```

좋아요 2개가 눌렸는데 카운트는 1만 올랐다. 이걸 **lost update**(갱신 유실)라고 한다.

---

## 3. 그래서 DB에게 계산을 맡긴다 — 벌크 UPDATE

```sql
update article set liked_count = liked_count + 1 where article_id = 7
```

`liked_count + 1` 의 계산 주체가 자바가 아니라 **DB**다. 값을 읽어서 가져올 필요가 없다. "지금 들어있는 값이 뭐든 거기에 1 더해라"라고 명령만 보낸다.

### 왜 원자적(atomic)인가

DB는 UPDATE 문 하나를 실행하는 동안 해당 **행에 잠금(lock)** 을 건다. 그 사이 다른 트랜잭션이 같은 행을 UPDATE하려 하면 **기다린다**.

```
시각  요청A                              요청B                              DB
 1    UPDATE ... +1  (행 잠금 획득)                                         129 → 130
 2                                       UPDATE ... +1  → 대기...          130
 3    커밋 (잠금 해제)                                                      130
 4                                       대기 끝, 실행 → 130+1             131  ← 둘 다 반영
```

읽기-계산-쓰기가 **DB 안에서 쪼개지지 않는 한 덩어리**로 일어나기 때문에 유실이 없다. 이것이 "원자적"의 뜻이다.

"벌크(bulk)"라고 부르는 이유: 엔티티를 하나씩 메모리에 올려 변경 감지로 처리하는 게 아니라, **조건에 맞는 행들을 SQL 한 방으로 한꺼번에** 바꾸기 때문이다. 지금은 PK 조건이라 1행이지만 분류는 벌크 연산이다.

---

## 4. `@Modifying` 이 뭔가

Spring Data JPA에서 `@Query` 는 **기본적으로 조회(SELECT)** 라고 가정하고 내부적으로 `getResultList()` 계열을 호출한다.

UPDATE/DELETE JPQL은 실행 방식이 다르다. `executeUpdate()` 로 실행해야 한다.

`@Modifying` = "이 쿼리는 조회가 아니라 데이터를 바꾼다. `executeUpdate()` 로 실행해라" 라는 표시다.

```java
@Modifying                                    // 없으면 실행 시 예외
@Query("update Article a set a.likedCount = a.likedCount + 1 where a.articleId = :id")
int increaseLikedCount(@Param("id") Long id);
```

빼먹으면 `InvalidDataAccessApiUsageException` 계열 예외가 난다.

---

## 5. 왜 리턴값이 1밖에 안 나오는가

`executeUpdate()` 의 반환값 정의가 **"이 문장으로 영향받은 행의 개수"** 다. JDBC 시절부터의 규약이고 JPA도 따른다.

SQL의 UPDATE는 **갱신된 값을 돌려주지 않는다.** 돌려주는 건 "몇 행 건드렸는지" 뿐이다.

```java
int result = articleRepository.increaseLikedCount(7L);
// article_id = 7 은 PK라 딱 1행 → result = 1
// 그 게시글이 없으면 → result = 0
```

좋아요가 129개든 5개든 **행 1개를 갱신했으니 언제나 1**. 카운트와 무관한 숫자다.

그런데 코드가 이 값을 카운트로 쓴다.

```java
likeCount = articleService.increaseLikedCount(article.getArticleId());   // 1
return ArticleLikeResponse.of(true, likeCount);                          // {"likeCount": 1}
```

"행 수"를 "좋아요 수"로 착각해서 응답에 실은 것이 버그의 정체다.

참고: PostgreSQL 등 일부 DB는 `UPDATE ... RETURNING liked_count` 로 갱신된 값을 받는 확장 문법이 있다. 표준 SQL도 아니고 JPQL도 지원하지 않는다. 현재 H2/JPA 조합에선 못 쓴다.

---

## 6. 경로마다 값이 다른 문제

`ArticleLikeService.like()` 는 분기가 둘인데 서로 다른 종류의 값을 반환한다.

```java
int likeCount = article.getLikedCount();          // ← 실제 카운트 (129)
if (!이미 좋아요) {
    save(...);
    likeCount = increaseLikedCount(...);          // ← 행 수 (1)
}
```

- 처음 누름 → `1`
- 이미 누른 상태에서 또 누름 → `129` (엔티티 값, 정확)

같은 API가 상황에 따라 1 또는 실제값을 준다. 프론트가 이 값을 화면에 쓰면 좋아요 누르는 순간 129 → 1로 튄다.

`unlike()` 도 같다. 항상 `1`. `liked_count > 0` 가드에 걸려 0행 갱신되면 `0`.

---

## 7. "영속성 컨텍스트를 우회한다"는 게 무슨 뜻

벌크 UPDATE는 JPA가 SQL을 **DB로 바로 보낸다.** 메모리에 들고 있는 자바 객체는 쳐다보지 않는다.

```java
Article article = articleService.findArticleByUuid(uuid);   // 메모리에 article 객체(likedCount=129)
articleService.increaseLikedCount(article.getArticleId());  // DB만 130으로 바뀜

article.getLikedCount();   // 129  ← 메모리 객체는 그대로
```

```
   [영속성 컨텍스트(메모리)]              [DB]
    article#7 { likedCount: 129 }        liked_count: 130
              ↑ 안 바뀜                        ↑ 바뀜
```

JPA는 자기가 관리하는 객체의 변경은 추적하지만, 직접 쏜 SQL이 어떤 객체에 영향을 주는지는 모른다. 그래서 둘이 어긋난다. 이 상태를 **stale(낡음)** 이라 한다.

결론: `like()` 안에서 정확한 카운트를 알려면 메모리 객체를 믿을 수 없고 **DB에 다시 물어야** 한다.

---

## 8. 스칼라 조회란

**엔티티 조회** — 행 전체를 엔티티 객체로 가져온다.

```java
@Query("select a from Article a where a.articleId = :id")
Article findEntity(Long id);
```

함정: 영속성 컨텍스트에 이미 `article#7` 객체가 있으면, JPA는 DB에서 행을 읽어와도 **기존 객체를 그대로 돌려준다**. 같은 트랜잭션에서 같은 엔티티는 같은 객체여야 한다는 원칙 때문이다. 결과적으로 `likedCount` 는 여전히 129. 다시 조회했는데도 옛 값인 이유.

**스칼라 조회** — 엔티티가 아니라 **값 하나**만 가져온다.

```java
@Query("select a.likedCount from Article a where a.articleId = :id")
int findLikedCount(Long id);
```

결과가 `int` 다. 엔티티가 아니니 "캐시에 있는 객체로 대체" 규칙이 적용되지 않는다. **DB의 값이 그대로 온다** → 130.

"스칼라(scalar)" = 객체나 목록이 아닌 단일 값. 숫자·문자열 하나.

---

## 9. 전체 흐름 비교

현재 (버그):

```
1. 좋아요 존재 확인
2. ArticleLike 저장
3. UPDATE liked_count = liked_count + 1   → DB 130, 반환값 1(행 수)
4. 그 1을 likeCount로 응답                → {"likeCount": 1}   ❌
```

수정안 A:

```
1. 좋아요 존재 확인
2. ArticleLike 저장
3. UPDATE liked_count = liked_count + 1   → DB 130 (반환값 무시)
4. SELECT liked_count → 130 (스칼라 조회)
5. 응답                                   → {"likeCount": 130}  ✅
```

원자적 증감(3단계)을 유지하면서 정확한 값은 별도 조회(4단계)로 얻는다. SELECT 한 번 비용으로 정확성과 동시성을 둘 다 챙긴다. 분기에 상관없이 항상 같은 종류의 값을 반환하므로 6번 문제도 함께 사라진다.

```java
// ArticleRepository
@Query("select a.likedCount from Article a where a.articleId = :id")
int findLikedCount(@Param("id") Long id);
```

```java
// ArticleLikeService
@Transactional
public ArticleLikeResponse like(Member member, Article article) {
    if (!articleLikeRepository.existsByArticleAndMember(article, member)) {
        articleLikeRepository.save(ArticleLike.of(article, member));
        articleService.increaseLikedCount(article.getArticleId());
    }
    return ArticleLikeResponse.of(true, articleService.findLikedCount(article.getArticleId()));
}
```

### `@Modifying` 의 옵션

- `flushAutomatically = true` — UPDATE 실행 전에, 아직 DB에 안 보낸 변경사항을 먼저 밀어낸다
- `clearAutomatically = true` — UPDATE 후 영속성 컨텍스트를 비운다. 이후 엔티티 조회가 DB를 새로 읽어 stale 문제가 사라진다. 대신 그 트랜잭션이 들고 있던 다른 엔티티도 모두 detach되니 주의

---

## 10. 대안

**B. 엔티티 변경 감지 + 낙관적 락**

```java
article.increaseLikedCount();   // this.likedCount++ → 변경 감지로 UPDATE
```

`article.getLikedCount()` 가 정확해지고 재조회가 필요 없다. 대신 2번의 lost update로 돌아간다. `@Version` 필드를 붙여 **낙관적 락**을 쓰면 동시 수정 시 두 번째 트랜잭션이 예외로 튕기므로 재시도 처리가 필요하다.

참고: `ArticleVoteCount` 엔티티에 `version` 필드가 이미 있지만 `@Version` 어노테이션이 안 붙어 있어 락으로 동작하지 않는다. 별도 검토 대상.

**C. 응답에서 `likeCount` 제거**

프론트가 낙관적으로 ±1 처리. 스펙 변경이고 새로고침 시 값이 어긋날 수 있다.

---

## 11. 상태

- 수정 미적용. 서비스 로직 변경이라 승인 대기
- 스웨거 예시는 `likeCount: 4` / `3` 으로 **정상 동작 기준**으로 작성돼 있음 → A안을 적용하면 문서가 그대로 맞는다
- 적용 전까지 문서와 구현이 이 지점에서 어긋난 상태임을 인지할 것
