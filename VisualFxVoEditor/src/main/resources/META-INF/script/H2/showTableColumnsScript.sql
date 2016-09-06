SELECT
b.column_name, 
case when s. constraint_type = 'PRIMARY KEY' then 'PRI' else '' end  COLUMN_KEY,
b.is_nullable,
b.ordinal_position,
b.TYPE_NAME,
b.CHARACTER_MAXIMUM_LENGTH
from 
information_schema.columns b left outer join information_schema.constraints s
on b.table_catalog = s.constraint_catalog
and b.table_schema = s.constraint_schema
and b.table_name = s.table_name
and b.column_name = s.column_list
where b.table_name = ':tableName'
and b.table_schema = ':databaseName'