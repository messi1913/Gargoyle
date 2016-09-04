/*2016.2.1 DBA_TAB_COLUMNS, DBA_TABLES 에서 ALL_TAB_COLUMNS, ALL_TABLES... 등으로 변경 특정계정에서는 조회가 안됨.*/


/*오라클 테이블생성 스크립트 생성기 쿼리*/
SELECT ':tableName' as tbName , C FROM (
SELECT
TABLE_NAME Y,
0 X ,
        'CREATE TABLE ' ||RTRIM(TABLE_NAME) ||'(' AS C
  FROM  ALL_TABLES
 WHERE  1=1
 #if($databaseName)
AND OWNER = UPPER(:databaseName)
#end
AND TABLE_NAME = :tableName
UNION
SELECT
TC.TABLE_NAME Y,
COLUMN_ID X,
			RTRIM(DECODE(COLUMN_ID,1,NULL,','))
        || RTRIM(COLUMN_NAME)|| ' '
        || RTRIM(DATA_TYPE)
        || RTRIM(DECODE(DATA_TYPE,'DATE',NULL,'LONG',NULL,'NUMBER',DECODE(TO_CHAR(DATA_PRECISION),NULL,NULL,'('),'('))
        || RTRIM(DECODE(DATA_TYPE,'DATE',NULL,'CHAR',DATA_LENGTH,'VARCHAR2',DATA_LENGTH,'NUMBER',DECODE(TO_CHAR(DATA_PRECISION),NULL,NULL,TO_CHAR(DATA_PRECISION)
        || ','
        || TO_CHAR(DATA_SCALE)),'LONG',NULL,'******ERROR'))
        || RTRIM(DECODE(DATA_TYPE,'DATE',NULL,'LONG',NULL,'NUMBER',DECODE(TO_CHAR(DATA_PRECISION),NULL,NULL,')'),')'))
        || ' '
        || RTRIM(DECODE(NULLABLE,'N','NOT NULL',NULL)) AS C
  FROM  ALL_TAB_COLUMNS TC
        ,ALL_OBJECTS O
 WHERE  O.OWNER = TC.OWNER
  AND  O.OBJECT_NAME = TC.TABLE_NAME
  AND O.OBJECT_TYPE = 'TABLE'
  #if($databaseName)
  AND O.OWNER = UPPER(:databaseName)
  #end
  AND TC.TABLE_NAME = :tableName
UNION
SELECT
TABLE_NAME Y,
999999 X,
      ')' || CHR(10)
            -- ||' STORAGE(' || CHR(10)
            -- ||' INITIAL ' || INITIAL_EXTENT || CHR(10)
            -- ||' NEXT ' || NEXT_EXTENT || CHR(10)
            -- ||' MINEXTENTS ' || MIN_EXTENTS || CHR(10)
            -- ||' MAXEXTENTS ' || MAX_EXTENTS || CHR(10)
            -- ||' PCTINCREASE '|| PCT_INCREASE || ')' ||CHR(10)
            -- ||' INITRANS ' || INI_TRANS || CHR(10)
            -- ||' MAXTRANS ' || MAX_TRANS || CHR(10)
            -- ||' PCTFREE ' || PCT_FREE || CHR(10)
            -- ||' PCTUSED ' || PCT_USED || CHR(10)
            -- ||' PARALLEL (DEGREE ' || DEGREE || ') ' || CHR(10)
            -- ||' TABLESPACE ' || RTRIM(TABLESPACE_NAME) ||CHR(10)
            ||';'||CHR(10)||CHR(10) AS C
  FROM  ALL_TABLES
 WHERE  1=1
#if($databaseName)
AND OWNER = UPPER(:databaseName)
#end
AND TABLE_NAME = :tableName
 ORDER  BY 1,2
)



