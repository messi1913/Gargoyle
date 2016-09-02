SELECT CASE
            WHEN B.CONSTRAINT_TYPE = 'P' THEN 'PRI'
            ELSE B.CONSTRAINT_TYPE
         END
            COLUMN_KEY,
         A.COLUMN_NAME,
         A.COLUMN_ID AS ORDINAL_POSITION,
         A.NULLABLE AS IS_NULLABLE,
         A.*
    FROM ALL_TAB_COLUMNS A,
         (SELECT C.TABLE_NAME,
                 C.COLUMN_NAME,
                 S.CONSTRAINT_TYPE,
                 C.OWNER
            FROM ALL_CONS_COLUMNS C, ALL_CONSTRAINTS S
           WHERE     C.CONSTRAINT_NAME = S.CONSTRAINT_NAME
                 AND C.TABLE_NAME = :tableName
                 AND C.OWNER = S.OWNER
                 AND S.CONSTRAINT_TYPE = 'P'
                 ) B
   WHERE     1 = 1
         AND A.TABLE_NAME = :tableName
         #if($databaseName)
         AND A.OWNER = :databaseName
         #end
         AND A.COLUMN_NAME = B.COLUMN_NAME(+)
         AND A.OWNER = B.OWNER(+)
         AND A.TABLE_NAME = B.TABLE_NAME(+)
ORDER BY ORDINAL_POSITION

