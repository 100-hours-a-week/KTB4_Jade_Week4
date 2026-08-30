SELECT 'before' AS phase, COUNT(*) AS article_count FROM article;
SHOW INDEX FROM article WHERE Key_name = 'idx_article_created_at';

ALTER TABLE article
    DROP INDEX idx_article_created_at,
    ADD INDEX idx_article_created_at (article_id, created_at);

ANALYZE TABLE article;

SELECT 'after' AS phase, COUNT(*) AS article_count FROM article;
SHOW INDEX FROM article WHERE Key_name = 'idx_article_created_at';

