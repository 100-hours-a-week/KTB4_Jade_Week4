SELECT
    COUNT(*) AS article_count,
    SUM(deleted_at IS NULL) AS active_count,
    SUM(member_id IS NULL) AS null_member_count,
    COUNT(DISTINCT member_id) AS distinct_member_count,
    MIN(created_at) AS oldest_created_at,
    MAX(created_at) AS newest_created_at
FROM article;

SHOW INDEX FROM article;

SELECT
    @@version AS mysql_version,
    @@innodb_buffer_pool_size AS innodb_buffer_pool_size_bytes;

SHOW CREATE TABLE article;

