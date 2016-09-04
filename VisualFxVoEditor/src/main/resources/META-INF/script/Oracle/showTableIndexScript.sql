/*2016.2.1 하드코딩되어있던 (잘못되있던 부분수정) */

SELECT CASE
            WHEN B.CONSTRAINT_TYPE = 'P' THEN 'PRI'
            ELSE ''
         END
            CONSTRAINT_TYPE,
             A.COLUMN_NAME,
         A.INDEX_NAME AS CONSTRAINT_NAME,
         A.*
    FROM ALL_IND_COLUMNS A
    LEFT JOIN ALL_CONSTRAINTS B
     ON   A.TABLE_NAME = B.TABLE_NAME
         AND B.CONSTRAINT_NAME = A.INDEX_NAME     
		 #if($databaseName)
		 AND B.OWNER =  ':databaseName'
		 #end
   WHERE     1 = 1
         AND A.TABLE_NAME = ':tableName'
		 #if($databaseName)
         AND A.INDEX_OWNER =':databaseName'
         AND A.TABLE_OWNER = ':databaseName'
		 #end
         AND B.CONSTRAINT_TYPE <> 'C' /*NOT NULL 제약조건*/
ORDER BY DECODE( CONSTRAINT_TYPE, 'PRI', 0, CONSTRAINT_TYPE ), A.INDEX_NAME, A.COLUMN_POSITION
