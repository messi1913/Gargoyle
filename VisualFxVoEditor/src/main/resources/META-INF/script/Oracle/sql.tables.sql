SELECT TABLE_NAME, '' COMMENTS FROM
ALL_TABLES
WHERE 1=1
#if($databaseName)
AND OWNER = :databaseName
#end
AND TABLE_NAME LIKE '%' || :tableName || '%'