# AWS 콘솔 작업 지시서 — S3 + CloudFront

리전은 전부 **아시아 태평양(서울) ap-northeast-2**.

## 1. 버킷 2개 생성

- `ktb-banteum-dev`
- `ktb-banteum-prod`

(이름 선점됐으면 뒤에 짧은 접미사. 기존에 만든 `ktb-banteum-770457184239-...`는 이름에 계정 ID가 박혀 있어 지우고 새로 만드는 게 나음. 객체 0개라 삭제해도 잃을 것 없음.)

**"모든 퍼블릭 액세스 차단"은 4개 전부 켠 채로 그대로 둘 것.** 건드리지 마세요. CloudFront를 쓰면 버킷을 공개할 필요가 없습니다.

나머지 기본값 그대로 생성.

## 2. CloudFront 배포 2개 생성

버킷당 하나씩. CloudFront → 배포 생성.

| 항목 | 값 |
|---|---|
| 원본 도메인 | 해당 S3 버킷 선택 |
| 원본 액세스 | **Origin access control settings (OAC)** → "제어 설정 생성" → 기본값으로 생성 |
| 뷰어 프로토콜 정책 | **Redirect HTTP to HTTPS** |
| 허용 HTTP 메서드 | **GET, HEAD** |
| 캐시 정책 | `CachingOptimized` |
| WAF | **비활성화** (유료) |
| 가격 분류 | 아시아 포함되는 것 (전체 또는 200) |
| 기본 루트 객체 | 비워둠 |

생성 직후 **"S3 버킷 정책을 업데이트해야 합니다"** 배너가 뜸 → **정책 복사** 버튼 클릭 → 해당 S3 버킷 → 권한 탭 → 버킷 정책 → 붙여넣기 → 저장.

이게 CloudFront만 버킷을 읽게 해주는 정책임. 직접 작성할 필요 없이 콘솔이 만들어줍니다.

배포 완료(Deployed)까지 5~10분. 완료되면 **배포 도메인 이름** 확인:

```
d1a2b3c4d5e6f7.cloudfront.net
```

dev/prod 각각 메모.

## 3. 버킷 CORS 설정 (양쪽 버킷 다)

각 버킷 → 권한 탭 → CORS → 편집:

```json
[{
  "AllowedHeaders": ["*"],
  "AllowedMethods": ["PUT"],
  "AllowedOrigins": ["http://localhost:5173", "http://localhost:5500", "http://127.0.0.1:5500"],
  "ExposeHeaders": []
}]
```

**이거 빼먹으면 업로드가 CORS 에러로 전부 막힙니다.** 읽기는 CloudFront가 담당하지만, 업로드(PUT)는 브라우저가 S3로 직접 쏘기 때문에 버킷 CORS가 여전히 필요합니다. prod 버킷은 나중에 실제 프론트 도메인을 `AllowedOrigins`에 추가.

## 4. IAM 사용자 + 액세스 키

IAM → 사용자 생성 → 이름 `ktb-banteum-s3-uploader`

- **콘솔 액세스 권한 주지 말 것** (프로그래밍 방식만)
- 권한: 직접 정책 연결 → 정책 생성 → JSON:

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": "s3:PutObject",
    "Resource": [
      "arn:aws:s3:::ktb-banteum-dev/*",
      "arn:aws:s3:::ktb-banteum-prod/*"
    ]
  }]
}
```

`AmazonS3FullAccess` 같은 관리형 정책은 붙이지 마세요. 키가 유출되면 버킷 전체를 삭제당할 수 있습니다.

생성 후 → 보안 자격 증명 → **액세스 키 만들기** → 사용 사례 "로컬 코드".

**Secret access key는 그 화면을 벗어나면 다시 볼 수 없습니다.** 안전한 곳에 보관하세요. 채팅이나 문서에 붙여넣지 마세요.

로컬 터미널에서:

```bash
aws configure
```

→ 키 2개 입력, region `ap-northeast-2`, format `json`

## 5. 비용 안전장치

- Billing → Budgets → 월 **$1** 예산 + 이메일 알림
- `ktb-banteum-dev` → 관리 탭 → 수명 주기 규칙 → **30일 후 만료(삭제)**

CloudFront 프리티어는 월 1TB 전송 + 1,000만 요청이라 이 규모에선 사실상 안 닿음.

---

## 확정된 값

```
dev 버킷 이름          : ktb-banteum-dev
prod 버킷 이름         : ktb-banteum-prod
dev CloudFront 도메인  : d28cyp412uo4kv.cloudfront.net
prod CloudFront 도메인 : d1u8qquxeairhj.cloudfront.net
IAM 사용자             : ktb-banteum-s3-uploader
```

`application-dev.yml` 기본값에 dev 값이 반영되어 있으므로, 로컬은 별도 환경변수 없이 동작합니다.

### GitHub Secrets (prod 배포용)

| Name | Value |
|---|---|
| `AWS_S3_BUCKET` | `ktb-banteum-prod` |
| `AWS_S3_REGION` | `ap-northeast-2` |
| `AWS_S3_PUBLIC_BASE_URL` | `https://d1u8qquxeairhj.cloudfront.net` |
| `AWS_S3_PRESIGNED_EXPIRATION` | `5m` |

액세스 키는 Secrets에 넣지 않습니다. 운영 서버는 EC2/ECS에 IAM 역할을 붙이면 코드 수정 없이 동작합니다.

---

## 참고 (코드 쪽, 작업 불필요)

- 이미 짜둔 코드는 `AWS_S3_PUBLIC_BASE_URL` 값만 CloudFront 도메인으로 바꾸면 그대로 동작함. 수정할 코드 없음
- 업로드용 presigned URL은 S3 엔드포인트로 발급되고, 조회용 URL만 CloudFront를 탐 — 정상 구성
- 파일 키가 매번 UUID라 같은 URL이 덮어써질 일이 없음 → **캐시 무효화 신경 쓸 필요 없음**
- 운영 서버(EC2/ECS) 올릴 땐 액세스 키 대신 IAM 역할 부착. 코드 수정 없이 동작
