/* VO에디터에서 테이블뷰에서 사용됨.*/

SELECT '' AS comments,
a.column_name,
a.type_name AS type ,
a.is_nullable as null_yn ,
a.CHARACTER_MAXIMUM_LENGTH AS DATA_LENGTH,
(  SELECT 'Y'
   FROM INFORMATION_SCHEMA.CONSTRAINTS S
   WHERE 1=1
   AND S.CONSTRAINT_TYPE = 'PRIMARY KEY'
   AND S.TABLE_NAME = ':tableName'
   AND S.COLUMN_LIST = A.COLUMN_NAME
   AND S.TABLE_CATALOG = A.TABLE_CATALOG
   AND S.table_schema = a.table_schema ) AS PK
FROM INFORMATION_SCHEMA.COLUMNS a
where 1=1
and a.table_catalog = database()
and a.table_schema = schema()
and a.table_name = ':tableName'
order by ordinal_position


