/* VO에디터에서 테이블뷰에서 사용됨.*/

SELECT CASE WHEN B.CONSTRAINT_TYPE = 'P' THEN 'PRI'
            ELSE B.CONSTRAINT_TYPE END PK,
         A.COLUMN_NAME,
	 A.COLUMN_ID AS ORDINAL_POSITION,
         A.NULLABLE AS IS_NULLABLE,
         '' AS COMMENTS,
         A.DATA_TYPE AS TYPE,
         A.DATA_LENGTH

    FROM ALL_TAB_COLUMNS A,
         (SELECT C.TABLE_NAME,
                 C.COLUMN_NAME,
                 S.CONSTRAINT_TYPE,
                 C.OWNER
            FROM ALL_CONS_COLUMNS C, ALL_CONSTRAINTS S
           WHERE     C.CONSTRAINT_NAME = S.CONSTRAINT_NAME
                  AND C.TABLE_NAME = ':tableName'
                 AND C.OWNER = S.OWNER
                 AND S.CONSTRAINT_TYPE = 'P'
                 ) B
   WHERE   1 = 1
        #if($databaseName)
         AND A.OWNER = ':databaseName'
         #end
         AND A.TABLE_NAME = ':tableName'
         AND A.COLUMN_NAME = B.COLUMN_NAME(+)
         AND A.OWNER = B.OWNER(+)
         AND A.TABLE_NAME = B.TABLE_NAME(+)
ORDER BY ORDINAL_POSITION