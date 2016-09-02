/* VO에디터에서 테이블뷰에서 사용됨.*/

SELECT '' AS comments,
a.column_name,
a.data_type AS type ,
a.is_nullable as null_yn ,
a.CHARACTER_MAXIMUM_LENGTH AS DATA_LENGTH,
(  SELECT 'Y'
   FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE C, INFORMATION_SCHEMA.TABLE_CONSTRAINTS S
   WHERE 1=1
   AND C.CONSTRAINT_NAME = S.CONSTRAINT_NAME
   AND S.CONSTRAINT_TYPE = 'PRIMARY KEY'
   AND C.TABLE_NAME = :tableName
   AND C.COLUMN_NAME = A.COLUMN_NAME
   AND C.TABLE_CATALOG = A.TABLE_CATALOG
   AND C.table_schema = a.table_schema ) AS PK
FROM INFORMATION_SCHEMA.COLUMNS a
where 1=1
and a.table_catalog = current_database()
and a.table_schema = current_schema()
and a.table_name = :tableName
order by ordinal_position