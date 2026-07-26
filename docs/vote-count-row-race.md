# 집계 row 생성 레이스

대상 코드: `domain/articleVote/service/ArticleVoteService.java`, `domain/articleVote/service/count/VoteCountUpdater.java`, `domain/articleVote/service/count/PessimisticLockVoteCountUpdater.java`, `domain/articleVote/repository/ArticleVoteCountRepository.java`, `common/config/DataInitializer.java`

한 줄 요약: 투표 요청 경로의 `findByIdForUpdate(...).orElseGet(() -> save(...))` 는 **check-then-act INSERT 레이스**다. 갱신 방식(비관적/낙관적/원자적/…)과 무관하게 6종 어디서든 똑같이 터지고, 측정 지표에도 안 잡힌다. 관찰이 아니라 **셋업 단계에서 제거**한다.

투표 도메인 코드에는 주석을 두지 않는다. 설계 의도와 배경은 이 문서가 담당한다.

---

## 1. 투표 경로 전체 흐름

### 진입

`ArticleVoteFacadeService.vote()` 에서 `@Transactional` 이 열린다. UUID 로 게시글을 조회한 뒤 `ArticleVoteService.vote()` 로 위임한다. 트랜잭션 하나가 게시글 조회부터 카운트 갱신까지 전부 감싼다.

### 분기 판단 — `ArticleVoteService.vote()`

`findByArticleAndMember` 로 이 회원의 기존 투표를 조회하고 결과에 따라 셋으로 갈린다.

| 상황 | 처리 | 응답 |
|---|---|---|
| 기존 투표 없음 | `ArticleVote` insert + `updater.increase()` | `voted=true, isNew=true` |
| 다른 선택지로 변경 | `changeOptionTo()` true + `updater.moveTo()` | `voted=true, isNew=false` |
| 같은 선택지 재투표 | 카운트 손 안 댐, 현재 값만 조회 | `voted=false` |

같은 회원의 중복 투표는 `(member_id, article_id)` 유니크 제약이 막는다. 서비스 분기는 "카운트를 어떻게 움직일지"만 결정하고, 집계값 경합은 `VoteCountUpdater` 가 처리한다.

### 락 구간 — `PessimisticLockVoteCountUpdater`

```java
ArticleVoteCount voteCount = lock(articleId);   // select ... for update
voteCount.increase(option);                     // 자바 필드 ++
```

`findByIdForUpdate` 에 `@Lock(PESSIMISTIC_WRITE)` 가 붙어 `select ... for update` 로 나간다. 해당 `article_vote_count` row 에 X 락이 걸리고, 같은 게시글의 동시 요청은 여기서 줄을 선다. 앞 트랜잭션이 커밋해야 다음이 진행된다.

락을 잡은 뒤에는 엔티티 필드를 직접 증감한다. `save()` 호출은 없다. 더티 체킹이 커밋 시점에 UPDATE 를 발행한다. 읽기-계산-쓰기 전 구간이 락 안에 있으므로 lost update 가 생기지 않는다.

`moveTo` 는 한쪽 -1, 다른 쪽 +1 이다. `Math.max(0, ...)` 로 하한을 0 으로 두어 집계가 이미 어긋난 상태에서도 음수로 내려가지 않는다.

### 트랜잭션 전파

`PessimisticLockVoteCountUpdater` 는 클래스 레벨에 `@Transactional(propagation = MANDATORY)` 를 단다.

락 수명 = 트랜잭션 수명이다. 여기서 새 트랜잭션을 열면 메서드 반환 직후 커밋되며 락이 풀려, 호출자가 아직 작업 중인데 다른 요청이 끼어든다. MANDATORY 는 새 트랜잭션 생성을 막고, 호출자 트랜잭션이 없으면 즉시 예외를 던진다. 트랜잭션 밖 호출이라는 실수가 조용히 통과하지 않는다.

### row 존재 전제

`lock()` 은 row 가 없으면 `IllegalStateException` 을 던진다. 생성하지 않는다. 그 이유가 이 문서의 나머지다.

### 교체 구조

`ArticleVoteService` 는 `VoteCountUpdater` 인터페이스에만 의존한다. 스키마(`article_vote_count`)는 고정한 채 갱신 방식만 구현체로 교체해 비교하기 위한 경계다. 현재 구현체는 비관적 락 하나. 낙관적 락·원자적 UPDATE 등으로 갈아끼울 때 서비스 코드는 바뀌지 않는다.

`ArticleVoteCount.version` 은 지금 아무도 안 쓰는 일반 int 컬럼이다. 스키마를 미리 고정해두려고 둔 것이고, 낙관적 락 단계에서 `@Version` 으로 승격한다.

### 읽기 경로

`findVoteCount`, `findVoteCounts`, `findMyVote(s)` 는 락을 잡지 않고 `@Transactional(readOnly = true)` 로 동작한다. row 가 없으면 영속화하지 않는 0/0 객체를 돌려준다.

---

## 2. 무엇이 문제였나

```java
// 변경 전
private ArticleVoteCount lock(Long articleId) {
    return articleVoteCountRepository.findByIdForUpdate(articleId)
            .orElseGet(() -> articleVoteCountRepository.save(ArticleVoteCount.of(articleId)));
}
```

집계 row 가 없을 때 두 트랜잭션이 동시에 들어오면:

```
시각  요청A                              요청B                              article_vote_count
 1    SELECT ... FOR UPDATE → 없음                                          (row 없음)
 2                                       SELECT ... FOR UPDATE → 없음       (row 없음)
 3    INSERT article_id=7                                                   삽입
 4                                       INSERT article_id=7                PK 중복 → 예외
```

"확인했더니 없더라" 와 "그래서 만든다" 사이가 원자적이지 않다. 앞서 정리한 lost update 와 같은 구조의 문제인데, 대상이 UPDATE 가 아니라 INSERT 인 판본이다.

## 3. 왜 실험 대상이 아닌가

실험 축은 **경합 강도 · 동시성 수준 · 방식별 순위 역전**이고, 지표는 **TPS · 락 대기 시간 · 재시도 횟수**다. 이 버그는 그 축과 다른 차원에 있다.

- 갱신 방식과 무관하다. 7종 중 무엇을 끼워도 `orElseGet(save)` 를 요청 경로에 두면 똑같이 난다. 방식 간 **차이를 만들지 못하므로 비교에 기여하지 않는다**.
- 지표에 안 잡힌다. PK 중복은 예외로 튀어 요청 실패가 되지, 락 대기나 재시도 통계로 드러나지 않는다.
- 정상 경로에선 애초에 안 걸린다. 게시글 생성 시 집계 row 가 함께 만들어지므로(`ArticleService.createArticle`), 실험 중 이게 터졌다면 그건 **발견이 아니라 데이터 셋업 누락**이다.

즉 실험 도중 이게 발생하면 결과를 오염시키는 노이즈다. 그래서 측정 전에 제거한다.

## 4. 세 가지 선택지와 판단

### A. 별도 트랜잭션에서 INSERT 후 중복 예외 무시 — 기각

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void createIfAbsent(Long articleId) {
    try { repo.saveAndFlush(ArticleVoteCount.of(articleId)); }
    catch (DataIntegrityViolationException ignored) { }
}
```

흔한 관용구지만 **여기서는 자기 자신과 교착한다**.

MySQL InnoDB 의 기본 격리 수준 REPEATABLE READ 에서, 존재하지 않는 row 에 대한 `SELECT ... FOR UPDATE` 는 그 자리(gap)에 **갭 락**을 건다. 바깥 트랜잭션이 갭 락을 쥔 상태에서 `REQUIRES_NEW` 로 연 **다른** 트랜잭션이 같은 자리에 INSERT 를 시도하면, 자기 바깥 트랜잭션의 갭 락에 막혀 `innodb_lock_wait_timeout`(기본 50초)까지 대기한다. 바깥은 안쪽을 기다리고 안쪽은 바깥의 락을 기다리는 자기 교착이다.

더 나쁜 점: 현재 개발/테스트 DB 인 H2 에는 갭 락이 없어 **테스트는 통과하고 MySQL 실험에서만 멈춘다**.

### B. `INSERT ... ON DUPLICATE KEY UPDATE` 를 락 조회보다 먼저 — 기각

레이스 자체는 없앤다(갭 락을 잡기 전에 INSERT 하므로 A 의 함정도 피한다). 하지만

- **모든 투표 요청마다 쓰기 한 번이 추가**된다. 이미 row 가 있는 정상 경로에도 붙는다. 측정하려는 게 갱신 방식별 비용인데 전 방식에 동일한 상수 비용을 얹는다.
- MySQL 전용 문법이다. H2 는 기본 모드에서 지원하지 않아 로컬/테스트와 실험 환경의 코드가 갈린다.

### C. 사전 생성 보장 + 요청 경로는 즉시 실패 — 채택

요청 경로에서 생성 자체를 없앤다. row 는 항상 그 전에 존재해야 하는 **전제 조건**으로 만든다.

- 정상 경로: 게시글 생성 시 동반 생성 (`ArticleService.createArticle`, 이미 있음)
- 초기 데이터: `DataInitializer.createVoteCounts()` 에서 게시글과 함께 생성
- 대량 실험 데이터: 데이터 생성 스크립트에서 게시글과 집계 row 를 함께 생성
- 그래도 없으면: 조용히 만들지 말고 `IllegalStateException` 으로 즉시 실패 → 셋업 누락이 측정값에 섞이지 않고 바로 드러남

정상 경로 쿼리 수는 변경 전과 동일하다. 추가 비용 0.

---

## 5. 적용 내용

**`PessimisticLockVoteCountUpdater`** — 생성 제거, fail-fast

```java
private ArticleVoteCount lock(Long articleId) {
    return articleVoteCountRepository.findByIdForUpdate(articleId)
            .orElseThrow(() -> new IllegalStateException("집계 row 누락: articleId=" + articleId));
}
```

**`ArticleService` / `DataInitializer`** — 생성 시 집계 row 보장

```java
@Transactional
public String createArticle(Member member, CreateArticleRequest request) {
    String articleUuid = UuidCreator.create(UuidPrefix.ARTICLE);
    Article article = Article.of(articleUuid, member, request);

    articleRepository.save(article);
    articleVoteCountRepository.save(ArticleVoteCount.of(article.getArticleId()));
    return article.getArticleUuid();
}
```

일반 게시글은 `ArticleService.createArticle()` 이 같은 트랜잭션에서 집계 row 를 함께 만든다. 초기 데이터는 운영 프로필을 제외한 `DataInitializer.createVoteCounts()` 가 생성된 모든 게시글의 집계 row 를 함께 만든다. 자동 누락 보정은 두지 않는다. 누락이 생기면 생성 또는 데이터 셋업의 불변조건 위반이므로 투표 요청에서 즉시 실패해 원인을 드러낸다.

**테스트** — `PessimisticLockVoteCountUpdaterTest.createsMissingRow` 를 `failsWhenRowMissing` 으로 교체. row 가 없을 때 `save` 를 부르지 않고 예외를 던지는지 검증한다.

---

## 6. 실험 데이터 세팅 체크리스트

대량 데이터(게시글 1000만 건 등)를 넣을 때:

1. 게시글과 **집계 row 를 같은 스크립트에서 함께** 생성한다. 게시글만 넣고 투표를 돌리면 전부 `IllegalStateException` 이다.
2. 세팅 후 누락 검증 — 결과는 반드시 0

```sql
select count(*)
from article a
left join article_vote_count c on c.article_id = a.article_id
where c.article_id is null;
```

3. 0 이 아니면 데이터 생성 스크립트를 수정하고 누락된 집계 row 를 별도 SQL 로 보정한 뒤 실험 데이터를 다시 검증한다.
4. 방식을 교체(비관적 → 낙관적 → …)해도 이 전제는 동일하다. `VoteCountUpdater` 구현체마다 다시 챙길 필요 없다.

## 7. 남는 것

- **읽기 경로는 여전히 관대하다.** `findVoteCount` / `findVoteCounts` 는 row 가 없으면 0/0 객체를 돌려준다. 목록 조회가 500 으로 죽지 않게 하려는 의도이고 DB 쓰기가 없으므로 레이스와 무관하다. 쓰기 경로만 엄격하다.
- **락 대기 타임아웃은 아직 미설정.** `findByIdForUpdate` 에 `@QueryHints` 가 없어 MySQL 기본 `innodb_lock_wait_timeout` 50초를 따른다. 고동시성 구간에서 대기가 쌓이면 커넥션 풀이 먼저 마른다. 실험 파라미터로 다룰지 별도 판단 필요.
- **`version` 컬럼은 여전히 미사용.** 낙관적 락 단계에서 `@Version` 으로 승격 예정. 스키마는 지금 고정돼 있다.
