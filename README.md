# ⚖️ 반틈(Banteum)

## Back-end 소개

두 가지 선택지 중 하나를 고르고 다른 사용자들의 생각을 확인할 수 있는 밸런스 게임 커뮤니티 프로젝트입니다.

Spring Boot 3.4.5와 Java 21로 서버를 구현하고, 운영 DB는 MySQL, 로컬·테스트 환경은 H2로 구성했습니다.

회원·인증, 밸런스 게임 게시글, 댓글, 좋아요, 투표, 이미지 업로드 기능을 구현했습니다. 이후 Docker·Nginx·GitHub Actions·AWS EC2를 이용한 블루/그린 배포 환경을 구축하고, 신규 컨테이너의 초기 응답 지연을 줄이기 위한 JVM 워밍업까지 적용했습니다.

도메인형 패키지 구조와 Controller-Service-Repository 계층을 사용했으며, 여러 도메인의 데이터를 조합하거나 트랜잭션 경계를 관리하는 로직은 Facade Service로 분리했습니다.

게시글 목록은 `createdAt + articleId` 복합 커서를 사용하는 무한 스크롤 방식으로 구현했습니다. 투표와 좋아요처럼 동시에 여러 요청이 발생할 수 있는 기능은 원자적 UPDATE와 비관적 락을 사용해 데이터 정합성을 보장했습니다.

## 개발 인원 및 기간

개발 기간 : 2026-06-07 ~ 2026-08-07  
개발 인원 : 프론트엔드/백엔드 1명 (본인)

## 사용 기술 및 Tools

| 구분 | 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.4.5, Spring Data JPA, Spring Security |
| Database | MySQL(운영), H2(로컬·테스트) |
| Auth | JWT Access Token, Refresh Token, Cookie, CSRF |
| Storage | AWS S3, CloudFront, Presigned URL |
| Infra / CI·CD | Docker, Docker Compose, Nginx, GitHub Actions, AWS EC2, OIDC, SSM, Blue/Green Deployment |
| API Docs | Swagger, Springdoc OpenAPI |
| Test | JUnit 5, Mockito, AssertJ, Spring Security Test |
| Load Test | Locust, JVM JIT Compilation Log |
| ETC | Caffeine Cache, Lombok, Gradle |

## Front-end

Front-end Github :

## 서비스 시연 영상

## 폴더 구조

<details>
<summary>폴더 구조 보기/숨기기</summary>

```text
src/main/java/kakaotech/task4
├── common
│   ├── baseEntity
│   ├── config
│   ├── exception
│   ├── resolver
│   ├── response
│   ├── security
│   └── uuid
└── domain
    ├── article
    │   ├── api
    │   ├── controller
    │   ├── dto
    │   ├── entity
    │   ├── repository
    │   └── service
    ├── articleLike
    ├── articleVote
    ├── auth
    ├── comment
    ├── member
    └── myInfo
```

</details>

## 서버 설계

도메인별로 Controller → Service → Repository 계층을 나누고, 인증·예외·응답·설정과 같은 공통 관심사는 `common` 패키지로 분리했습니다.

여러 도메인의 조회 결과를 하나의 응답으로 조립하거나 하나의 트랜잭션으로 처리해야 하는 기능은 Facade Service에서 조정하도록 구성했습니다.

| 도메인 | Controller | 주요 책임 |
| --- | --- | --- |
| 인증 | AuthController | 회원가입, 로그인, 토큰 재발급, 로그아웃, CSRF 토큰 발급 |
| 회원 | MyInfoController | 내 정보 조회, 기본 정보 수정, 비밀번호 수정, 회원 탈퇴 |
| 게시글 | ArticleController | 밸런스 게임 게시글 생성·조회·수정·삭제, 커서 페이지네이션 |
| 투표 | ArticleVoteController | 선택지 투표, 재투표, 투표 결과 집계 |
| 좋아요 | ArticleLikeController | 게시글 좋아요 등록·취소 |
| 댓글 | CommentController | 댓글 작성·수정·삭제 |
| 파일 | FileController | S3 이미지 업로드용 Presigned URL 발급 |

## 구현 기능

### Members / Auth

- 이메일·비밀번호·닉네임을 이용한 회원가입과 로그인 구현
- BCrypt를 이용한 비밀번호 암호화
- JWT Access Token과 Refresh Token을 쿠키로 전달
- Spring Security 필터 체인에서 JWT 인증 처리
- Refresh Token 저장소를 이용한 토큰 재발급 및 로그아웃 처리
- 쿠키 기반 인증에서 발생할 수 있는 CSRF 공격을 막기 위한 CSRF 토큰 검증 적용
- `@CurrentMember` ArgumentResolver를 이용해 인증된 회원을 컨트롤러에 주입
- 회원 탈퇴 시 데이터를 바로 제거하지 않고 soft delete 처리
- 닉네임 중복 확인 및 프로필·비밀번호 수정 기능 구현

### Articles

- 밸런스 게임 게시글 CRUD 구현
- 제목과 A/B 선택지를 포함하는 게시글 모델 설계
- `createdAt + articleId` 복합 커서를 이용한 무한 스크롤 구현
- 동일한 생성 시간을 가진 게시글이 있어도 누락이나 중복이 발생하지 않도록 정렬 기준 구성
- 게시글 목록 조회에 필요한 좋아요·투표·작성자 데이터를 일괄 조회해 N+1 문제 개선
- 게시글 카운터를 원자적 UPDATE 쿼리로 갱신
- 목록 정렬 조건에 맞춰 `(created_at, article_id)` 복합 인덱스 적용
- 작성자 여부, 내 좋아요 여부, 내 투표 선택지와 전체 투표 결과를 응답에 포함

### Votes

- 게시글의 A/B 선택지 투표 기능 구현
- 처음 투표하는 경우 투표 이력 생성 및 집계 증가
- 동일한 선택지에 다시 투표하면 집계를 변경하지 않도록 멱등 처리
- 다른 선택지로 변경하면 기존 선택지의 집계를 감소시키고 새 선택지의 집계를 증가
- `(member_id, article_id)` UNIQUE 제약으로 회원당 하나의 투표 이력만 허용
- `ArticleVoteCount`에 선택지별 집계 결과를 별도로 저장해 조회 비용 절감
- 비관적 락을 사용해 동시 투표 요청에서 발생할 수 있는 유실 업데이트 방지
- 게시글 생성 시 집계 row를 함께 생성해 최초 투표 시점의 row 생성 경쟁 제거
- 락 획득 순서를 통일해 중복 집계와 교착 가능성 감소

### Likes

- 게시글 좋아요 등록 및 취소 기능 구현
- 회원과 게시글의 관계를 별도 좋아요 엔티티로 관리
- 중복 좋아요를 방지하고 게시글의 좋아요 수와 실제 좋아요 이력을 함께 관리
- 좋아요 수가 음수로 내려가지 않도록 조건부 UPDATE 적용
- 목록 조회 시 현재 사용자가 좋아요한 게시글 ID를 한 번에 조회
- 벌크 UPDATE 이후 1차 캐시에 남은 이전 값이 반환되지 않도록 스칼라 쿼리로 최신 카운트 재조회

### Comments

- 게시글 댓글 작성·수정·삭제 구현
- 댓글 작성자만 수정 및 삭제할 수 있도록 권한 검증
- 삭제된 댓글이 조회 결과에 포함되지 않도록 처리
- 게시글 상세 조회 시 댓글과 작성자 정보를 일괄 조회해 N+1 문제 개선

### Image Upload

- AWS S3 Presigned URL을 이용한 이미지 직접 업로드 구현
- 서버는 업로드 URL과 CloudFront 조회 URL만 발급하고 이미지 파일은 클라이언트가 S3로 직접 전송
- JPEG, PNG, WEBP, GIF 형식만 허용하도록 Content-Type 검증
- CloudFront 도메인을 벗어난 이미지 URL이 회원 정보에 저장되지 않도록 검증
- 비인증 상태에서도 회원가입 이미지를 업로드할 수 있도록 API를 공개하되, IP별 발급 횟수 제한 적용
- S3 버킷을 비공개로 유지하고 CloudFront OAC를 통해서만 이미지 조회 허용

## 데이터베이스 설계

### 요구사항 분석

#### 회원 관리

- 이메일·비밀번호·닉네임과 프로필 이미지를 포함하는 회원 정보 관리
- 이메일·닉네임은 UNIQUE 제약으로 중복 방지
- 회원 탈퇴는 soft delete로 처리해 연관 데이터와 이력 보존

#### 게시글 관리

- 제목과 두 개의 선택지를 포함하는 밸런스 게임 게시글 관리
- 작성자와 게시글의 연관관계 설정
- 생성 시간과 게시글 ID를 기준으로 커서 페이지네이션 수행
- 좋아요와 투표 집계값을 게시글 목록에서 함께 제공

#### 투표 관리

- 회원과 게시글 조합당 하나의 투표 이력 저장
- 사용자가 선택한 A/B 선택지를 기록
- 게시글별 집계 row를 분리해 목록 조회 시 반복 집계 비용 절감
- 비관적 락으로 동시 투표 시 집계 데이터 정합성 보장

#### 좋아요 관리

- 회원과 게시글의 다대다 관계를 좋아요 엔티티로 분리
- 중복 좋아요를 방지하고 게시글의 좋아요 수와 실제 좋아요 이력을 함께 관리

#### 댓글 관리

- 댓글 내용과 작성자, 대상 게시글, 생성·수정 시간을 관리
- soft delete 정책을 적용해 삭제된 댓글을 일반 조회에서 제외

#### 인증 관리

- Access Token과 Refresh Token을 분리
- Refresh Token을 서버 저장소에서 관리해 재발급과 로그아웃 시 유효성 확인
- Access Token은 짧게, Refresh Token은 길게 유지하도록 만료 시간 분리

### 모델링 — E-R Diagram

## 배포 아키텍처

`master` 브랜치에 변경 사항이 반영되면 GitHub Actions가 테스트를 실행하고 Docker 이미지를 빌드해 레지스트리에 업로드합니다.

GitHub Actions는 장기 AWS Access Key 대신 OIDC를 이용해 임시 자격증명을 발급받으며, AWS SSM을 통해 EC2에 배포 명령을 전달합니다.

EC2에서는 현재 서비스 중이지 않은 blue 또는 green 컨테이너에 새 이미지를 배포합니다. 컨테이너가 정상 상태에 도달하면 내부 JVM 워밍업을 실행한 뒤 Nginx upstream을 새 컨테이너로 전환합니다.

전환에 실패하면 기존 컨테이너가 계속 요청을 처리하며, 성공 마커가 확인되지 않으면 GitHub Actions 배포 작업도 실패하도록 구성했습니다.

Nginx는 TLS 종료와 `/api` 리버스 프록시를 담당하고, 애플리케이션과 MySQL 컨테이너는 외부 포트를 직접 공개하지 않습니다.

### Architecture / CI·CD

## 트러블 슈팅

### 1.

### 2.

### 3.

## 프로젝트 후기

이 프로젝트는 단순한 게시판 CRUD에서 출발해 실제 서비스 운영에 필요한 데이터 정합성, 인증 보안, 조회 성능과 배포 안정성을 단계적으로 고민해 본 프로젝트입니다.

초기에는 데이터를 순수 Java 구조로 관리했지만, 기능이 늘어나는 과정에서 Spring Data JPA로 마이그레이션했습니다. 이후 단순히 동작하는 코드에 머무르지 않고 트랜잭션 경계를 정리하고, 목록과 댓글 조회의 N+1 문제를 개선하고, 복합 커서와 인덱스를 적용하면서 데이터가 늘어났을 때의 동작까지 고려했습니다.

특히 투표 기능을 구현하면서 단위 테스트를 통과하는 것과 실제 동시 요청에서도 데이터 정합성이 지켜지는 것은 다른 문제라는 점을 경험했습니다. 애플리케이션 레벨의 동기화 대신 DB의 비관적 락과 UNIQUE 제약을 활용하고, 집계 row의 생성 시점과 락 획득 순서를 조정하며 동시성 제어의 범위를 구체적으로 고민할 수 있었습니다.

배포 과정에서는 단순 자동 배포를 넘어 OIDC와 SSM을 이용한 자격증명 관리, 블루/그린 전환, 헬스체크와 롤백, 성공 마커 검증까지 적용했습니다. 또한 애플리케이션이 정상 기동됐다는 사실만으로 즉시 안정적인 응답을 보장할 수 없다는 점을 부하 테스트에서 확인했습니다. JIT 컴파일 로그와 반복 측정을 통해 기동 직후의 꼬리 지연을 분석하고, 트래픽 전환 전에 주요 API를 워밍업하는 방식으로 개선했습니다.

가장 크게 배운 점은 개선 결과보다 측정 근거가 중요하다는 것입니다. 부하 테스트 결과를 정리하는 과정에서 서로 다른 수집 절차의 로그를 비교하거나 근거가 없는 표본 수를 제시했던 부분을 다시 검토하고 수정했습니다. 원하는 결론에 맞춰 수치를 해석하기보다, 재현 가능한 측정 조건과 확인하지 못한 한계를 함께 기록하는 태도의 중요성을 배웠습니다.

앞으로는 현재의 단일 EC2 환경을 넘어 애플리케이션을 여러 인스턴스로 확장했을 때의 캐시·동시성 문제를 검증하고, 실제 운영 트래픽을 관측할 수 있는 모니터링 환경까지 보완하고 싶습니다.
