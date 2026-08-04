import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

// 대상: GET /api/articles (인증 필요, DB 조회 포함)
const BASE = __ENV.BASE_URL || 'http://app:8080/api';
const VUS = Number(__ENV.VUS || 20);
const DURATION = __ENV.DURATION || '3m';

// 기동 직후 구간을 따로 보기 위한 버킷별 응답시간
const first30s = new Trend('latency_0_30s', true);
const first60s = new Trend('latency_30_60s', true);
const after60s = new Trend('latency_after_60s', true);

export const options = {
    scenarios: {
        constant: {
            executor: 'constant-vus',
            vus: VUS,
            duration: DURATION,
            gracefulStop: '10s',
        },
    },
    // 초기 spike를 보려면 임계값으로 죽이지 않는다. 관찰만 한다.
    thresholds: {
        http_req_failed: ['rate<0.05'],
    },
    summaryTrendStats: ['avg', 'min', 'med', 'p(95)', 'p(99)', 'max'],
};

export function setup() {
    const suffix = Date.now();
    const email = `loadtest${suffix}@kakaotech.com`;
    const password = 'JadeHello1234!';
    const headers = { 'Content-Type': 'application/json' };

    const signUp = http.post(`${BASE}/auth/sign-up`, JSON.stringify({
        email,
        password,
        checkPassword: password,
        nickname: `lt${suffix % 100000}`,
        profileImageUrl: 'https://example.invalid/profile/loadtest.webp',
    }), { headers });
    check(signUp, { 'sign-up 2xx': (r) => r.status >= 200 && r.status < 300 });

    const signIn = http.post(`${BASE}/auth/sign-in`, JSON.stringify({ email, password }), { headers });
    check(signIn, { 'sign-in 2xx': (r) => r.status >= 200 && r.status < 300 });

    const cookie = signIn.cookies['access_token'];
    if (!cookie || cookie.length === 0) {
        throw new Error(`access_token 쿠키 없음. status=${signIn.status} body=${signIn.body}`);
    }

    return { accessToken: cookie[0].value, startedAt: Date.now() };
}

export default function (data) {
    const res = http.get(`${BASE}/articles?size=10`, {
        headers: { Cookie: `access_token=${data.accessToken}` },
        tags: { name: 'GET /articles' },
    });

    check(res, { 'articles 200': (r) => r.status === 200 });

    const elapsed = (Date.now() - data.startedAt) / 1000;
    if (elapsed <= 30) first30s.add(res.timings.duration);
    else if (elapsed <= 60) first60s.add(res.timings.duration);
    else after60s.add(res.timings.duration);
}
