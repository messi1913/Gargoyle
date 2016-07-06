SELECT TABLE_NAME, '' AS COMMENTS 
FROM INFORMATION_SCHEMA.tables 
WHERE 1=1  
AND TABLE_catalog = database() 
and table_schema = schema() 
and TABLE_NAME like '%:tableName%' 
order by TABLE_NAME