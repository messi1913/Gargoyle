SELECT TABLE_NAME, '' AS COMMENTS FROM INFORMATION_SCHEMA.tables
WHERE 1=1
AND TABLE_catalog = current_database()
#if($databaseName)
AND table_schema = :databaseName
#else
and table_schema = current_schema()
#end
and TABLE_NAME like '%' || :tableName || '%'
order by TABLE_NAME