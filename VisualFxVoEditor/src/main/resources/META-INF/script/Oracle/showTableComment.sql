SELECT
TABLE_NAME,
COMMENTS
FROM ALL_TAB_COMMENTS
WHERE 1=1
#if($databaseName)
AND OWNER = :databaseName
#end
AND TABLE_NAME LIKE '%' || :tableName || '%'
ORDER BY TABLE_NAME