SELECT 
COLUMN_NAME, 
DATA_TYPE AS TYPE, 
	CASE WHEN IS_NULLABLE = 'YES' THEN 'Y' 
	ELSE 'N' END NULL_YN,  
CHARACTER_MAXIMUM_LENGTH AS DATA_LENGTH,  
	CASE WHEN COLUMN_KEY = 'PRI' THEN 'Y' 
	END COLUMN_KEY,  
COLUMN_COMMENT AS COMMENTS ,
ORDINAL_POSITION,
IS_NULLABLE,
DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE 1=1 
#if($databaseName)
AND TABLE_SCHEMA = ':databaseName' 
#end
AND TABLE_NAME = ':tableName' 
ORDER BY ORDINAL_POSITION