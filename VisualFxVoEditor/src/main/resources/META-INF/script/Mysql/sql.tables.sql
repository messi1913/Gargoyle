SELECT
TABLE_NAME ,
TABLE_COMMENT AS COMMENTS
FROM INFORMATION_SCHEMA.TABLES
WHERE 1=1
AND TABLE_TYPE = 'BASE TABLE'
AND TABLE_SCHEMA = DATABASE()
AND TABLE_NAME LIKE concat('%', :tableName ,'%')
ORDER BY TABLE_NAME