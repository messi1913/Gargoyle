SELECT
tbl_name as TABLE_NAME,
"" as COMMENTS
from sqlite_master
where type='table'
AND tbl_name LIKE %:tableName%
ORDER BY tbl_name