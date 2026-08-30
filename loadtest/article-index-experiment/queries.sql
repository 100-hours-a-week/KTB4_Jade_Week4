-- 이 파일 전체를 한 번에 실행하지 않는다.
-- 먼저 커서만 준비한 뒤, 아래의 번호가 붙은 블록을 하나씩 복사해 실행한다.

SET @cursor_id = 9223372036854775807;

SELECT
    FROM_UNIXTIME(
        UNIX_TIMESTAMP(MIN(created_at))
        + (UNIX_TIMESTAMP(MAX(created_at)) - UNIX_TIMESTAMP(MIN(created_at))) * 0.5
    ),
    FROM_UNIXTIME(
        UNIX_TIMESTAMP(MIN(created_at))
        + (UNIX_TIMESTAMP(MAX(created_at)) - UNIX_TIMESTAMP(MIN(created_at))) * 0.1
    )
INTO @middle_cursor_created_at, @late_cursor_created_at
FROM article;

SELECT
    @middle_cursor_created_at AS middle_cursor_created_at,
    @late_cursor_created_at AS late_cursor_created_at,
    @cursor_id AS cursor_id;

-- 1. 첫 페이지 INNER JOIN
EXPLAIN
SELECT a.*, m.*
FROM article a
JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

EXPLAIN ANALYZE
SELECT a.*, m.*
FROM article a
JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

-- 2. 첫 페이지 LEFT JOIN
EXPLAIN
SELECT a.*, m.*
FROM article a
LEFT JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

EXPLAIN ANALYZE
SELECT a.*, m.*
FROM article a
LEFT JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

-- 3. 중간 커서 INNER JOIN
EXPLAIN
SELECT a.*, m.*
FROM article a
JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @middle_cursor_created_at
      OR (a.created_at = @middle_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

EXPLAIN ANALYZE
SELECT a.*, m.*
FROM article a
JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @middle_cursor_created_at
      OR (a.created_at = @middle_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

-- 4. 중간 커서 LEFT JOIN
EXPLAIN
SELECT a.*, m.*
FROM article a
LEFT JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @middle_cursor_created_at
      OR (a.created_at = @middle_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

EXPLAIN ANALYZE
SELECT a.*, m.*
FROM article a
LEFT JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @middle_cursor_created_at
      OR (a.created_at = @middle_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

-- 5. 후반 커서 INNER JOIN
EXPLAIN
SELECT a.*, m.*
FROM article a
JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @late_cursor_created_at
      OR (a.created_at = @late_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

EXPLAIN ANALYZE
SELECT a.*, m.*
FROM article a
JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @late_cursor_created_at
      OR (a.created_at = @late_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

-- 6. 후반 커서 LEFT JOIN
EXPLAIN
SELECT a.*, m.*
FROM article a
LEFT JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @late_cursor_created_at
      OR (a.created_at = @late_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

EXPLAIN ANALYZE
SELECT a.*, m.*
FROM article a
LEFT JOIN member m ON m.member_id = a.member_id
WHERE a.deleted_at IS NULL
  AND (
      a.created_at < @late_cursor_created_at
      OR (a.created_at = @late_cursor_created_at AND a.article_id < @cursor_id)
  )
ORDER BY a.created_at DESC, a.article_id DESC
LIMIT 10;

