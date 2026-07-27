# 프론트엔드 지시서 — 프로필 이미지 업로드 (S3 Presigned URL)

백엔드 구현 완료. 프론트에서 아래 흐름대로 붙이면 됨.

## 전체 흐름

```
1) POST /files/image-uploads   → 서버가 업로드 허가증(uploadUrl) + 최종 주소(fileUrl) 발급
2) PUT  <uploadUrl>            → S3에 이미지 바이너리 직접 업로드 (서버 안 거침)
3) PATCH /me/basic-info        → fileUrl을 프로필에 저장
4) GET  /me/basic-info         → 저장된 fileUrl로 <img> 표시
```

이미지 파일 자체는 백엔드 서버를 거치지 않는다. 서버는 업로드 권한만 발급하고, 최종 주소 문자열만 저장한다.

---

## 1단계. 업로드 URL 발급

```
POST /files/image-uploads
```

**로그인 없이 호출할 수 있다.** 회원가입 화면에서도 써야 하기 때문이다. CSRF 토큰도 불필요하다.

대신 **IP당 1시간에 10회**로 발급 횟수가 제한된다. 초과하면 `429 FILE-429-001`. 정상 사용에서는 걸리지 않지만, 업로드를 재시도 루프로 돌리지 말 것.

요청
```json
{ "contentType": "image/webp" }
```

응답 `200`
```json
{
  "status": "OK",
  "success": true,
  "data": {
    "uploadUrl": "https://ktb-banteum-dev.s3.ap-northeast-2.amazonaws.com/profile/3f2b....webp?X-Amz-Algorithm=...&X-Amz-Signature=...",
    "fileUrl": "https://d28cyp412uo4kv.cloudfront.net/profile/3f2b....webp"
  }
}
```

- `uploadUrl` — S3에 PUT할 임시 주소. **유효시간 5분.**
- `fileUrl` — 업로드 완료 후 실제로 이미지를 볼 수 있는 CloudFront 주소. 이 값을 서버에 저장한다.

### 허용 contentType

```
image/jpeg
image/png
image/webp
```

그 외는 `400 FILE-400-001`. SVG는 스크립트 실행이 가능해 의도적으로 제외했다.

### 파일명

서버가 `profile/{uuid}.{확장자}`로 정한다. 프론트가 파일명을 지정할 수 없고, 지정할 필요도 없다.

---

## 2단계. S3에 직접 업로드

```
PUT <uploadUrl>
Content-Type: image/webp     ← 1단계에서 보낸 contentType과 정확히 일치해야 함
Body: <파일 바이너리>
```

주의사항:

- **`Content-Type`이 1단계 요청값과 다르면 `403 SignatureDoesNotMatch`가 난다.** 서명에 포함된 값이라 반드시 같아야 한다.
- **인증 헤더를 붙이지 말 것.** `Authorization`, 쿠키 모두 불필요하다. 서명이 곧 인증이다. `credentials: 'include'`를 넣으면 오히려 실패한다.
- 응답 본문은 비어 있다. 상태 코드 `200`이면 성공.
- 5분이 지나면 `403`. 사용자가 업로드 창을 오래 열어뒀다면 1단계부터 다시 한다.

S3 버킷 CORS에 아래 오리진이 등록되어 있다. 다른 포트를 쓰면 백엔드에 알려달라.

```
http://localhost:5173
```

---

## 3단계. 프로필에 저장

```
PATCH /me/basic-info
```

```json
{ "profileImageUrl": "https://d28cyp412uo4kv.cloudfront.net/profile/3f2b....webp" }
```

- 1단계에서 받은 `fileUrl`을 **그대로** 보낸다.
- 서버가 이 주소가 우리 CloudFront 경로인지 검증한다. 임의의 외부 URL을 보내면 `400 FILE-400-002`.
- 닉네임만 바꿀 때는 `profileImageUrl`을 생략하면 된다 (둘 다 없으면 `400 MY-400-001`).

### 회원가입도 동일하다

`POST /auth/sign-up`의 `profileImageUrl`은 **필수값**이다. 가입 화면에서 1~2단계를 그대로 수행한 뒤, 받은 `fileUrl`을 가입 요청에 넣는다.

```json
{
  "email": "...",
  "password": "...",
  "checkPassword": "...",
  "nickname": "...",
  "profileImageUrl": "https://d28cyp412uo4kv.cloudfront.net/profile/xxx.webp"
}
```

1단계 API가 비인증 허용이라 로그인 전에도 호출된다. 가입 시에도 동일하게 CloudFront 주소인지 검증하므로, 임의 URL을 넣으면 `400 FILE-400-002`.

---

## 4단계. 표시

```jsx
<img src={profileImageUrl} alt="프로필" />
```

CloudFront가 서빙한다. 별도 인증 불필요. 캐싱이 걸려 있어 두 번째부터는 빠르다.

같은 이미지를 수정해도 파일명(UUID)이 매번 달라지므로 **캐시 무효화를 신경 쓸 필요가 없다.**

---

## 인증·CSRF

이 프로젝트는 JWT를 **쿠키**로 관리하고 CSRF 토큰을 함께 쓴다.

- 백엔드 API 호출 시 `credentials: 'include'` 필수 (단, 1단계 업로드 URL 발급은 예외 — 인증 불필요)
- 변경 요청(POST/PATCH/PUT/DELETE)은 `XSRF-TOKEN` 쿠키 값을 `X-XSRF-TOKEN` 헤더에 실어야 함
- 토큰이 없으면 `GET /auth/csrf`로 발급
- 헤더 누락 시 `403`

**S3로 보내는 PUT 요청에는 위 내용이 전부 해당되지 않는다.** 우리 서버가 아니다.

---

## 구현 예시

```js
async function uploadProfileImage(file) {
  // 0) 용량 체크 — 서버가 막지 못하므로 프론트에서 반드시 확인
  if (file.size > 5 * 1024 * 1024) {
    throw new Error('5MB 이하 이미지만 업로드할 수 있습니다.');
  }

  // 1) 업로드 URL 발급 (로그인·CSRF 불필요 — 가입 화면에서도 그대로 사용)
  const res = await fetch('/files/image-uploads', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ contentType: file.type }),
  });
  const { data } = await res.json();
  const { uploadUrl, fileUrl } = data;

  // 2) S3에 직접 업로드 (인증 헤더·쿠키 없음)
  const put = await fetch(uploadUrl, {
    method: 'PUT',
    headers: { 'Content-Type': file.type },   // 1)에서 보낸 값과 동일해야 함
    body: file,
  });
  if (!put.ok) throw new Error('이미지 업로드에 실패했습니다.');

  // 3) 프로필에 저장
  await fetch('/me/basic-info', {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      'X-XSRF-TOKEN': getCookie('XSRF-TOKEN'),
    },
    body: JSON.stringify({ profileImageUrl: fileUrl }),
  });

  return fileUrl;
}
```

### 권장: 업로드 전 webp 변환

presigned 방식이라 **서버가 파일 크기를 강제할 수 없다.** 프론트에서 리사이즈 + webp 변환을 넣으면 용량이 크게 줄고 로딩도 빨라진다.

```js
function toWebp(file, maxSize = 512) {
  return new Promise((resolve) => {
    const img = new Image();
    img.onload = () => {
      const scale = Math.min(1, maxSize / Math.max(img.width, img.height));
      const canvas = document.createElement('canvas');
      canvas.width = img.width * scale;
      canvas.height = img.height * scale;
      canvas.getContext('2d').drawImage(img, 0, 0, canvas.width, canvas.height);
      canvas.toBlob((blob) => resolve(blob), 'image/webp', 0.8);
    };
    img.src = URL.createObjectURL(file);
  });
}
```

변환 후 `blob.type`이 `image/webp`가 되므로, 1단계 `contentType`과 2단계 `Content-Type`에 그대로 쓰면 된다.

---

## 에러 대응

| 응답 | 코드 | 원인 | 조치 |
|---|---|---|---|
| `400` | `FILE-400-001` | 허용되지 않는 contentType | 지원 형식 안내 |
| `400` | `FILE-400-002` | 우리 CloudFront 주소가 아닌 URL 전송 | `fileUrl`을 그대로 보내는지 확인 |
| `400` | `MY-400-001` | 수정할 필드가 하나도 없음 | 요청 본문 확인 |
| `401` | `JWT-401-*` | 미로그인 / 토큰 만료 | 재로그인 또는 토큰 재발급 |
| `403` | `AUTH-403-002` | CSRF 헤더 누락 (프로필 저장 시) | `X-XSRF-TOKEN` 확인 |
| `429` | `FILE-429-001` | 발급 횟수 초과 (IP당 1시간 10회) | 잠시 후 재시도 안내 |
| `409` | `MY-409-001` | 닉네임 중복 | 안내 |

S3 PUT 실패 (우리 서버 응답이 아님)

| 상태 | 원인 | 조치 |
|---|---|---|
| `403 SignatureDoesNotMatch` | `Content-Type` 불일치 | 1단계와 동일한 값 사용 |
| `403 AccessDenied` | 5분 만료 | 1단계부터 재시도 |
| CORS 에러 | 오리진 미등록 | 사용 중인 포트를 백엔드에 알림 |

---

## 참고

- dev CloudFront: `https://d28cyp412uo4kv.cloudfront.net`
- prod CloudFront: `https://d1u8qquxeairhj.cloudfront.net`
- 업로드 후 프로필 저장(3단계)을 하지 않으면 S3에 파일만 남는다. 사용자가 취소하면 그냥 두면 되고, 서버가 주기적으로 정리한다.
