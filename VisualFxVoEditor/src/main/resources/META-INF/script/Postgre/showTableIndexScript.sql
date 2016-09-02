/*테이블인덱스정보조회*/

SELECT
tc.constraint_type,
tc.constraint_name,
kcu.column_name,
tc.table_schema
FROM
    information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
WHERE 1=1
AND tc.table_name=:tableName
and tc.constraint_type <> 'CHECK'
#if($databaseName)
and tc.table_schema=:databaseName
#end
