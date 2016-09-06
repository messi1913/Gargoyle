/*테이블 정보와 커멘트정보를 리턴함.*/
SELECT
n.nspname, c.relname as table_name, obj_description(c.oid) as comments
FROM pg_catalog.pg_class c inner join pg_catalog.pg_namespace n
on c.relnamespace=n.oid
WHERE 1=1
and c.relkind = 'r'
#if($databaseName)
and nspname=':databaseName'
#end
and c.relname = ':tableName'