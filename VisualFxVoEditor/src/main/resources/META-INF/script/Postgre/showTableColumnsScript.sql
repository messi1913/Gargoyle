/*테이블의 컬럼명, 기본키 ,널여부, 위치정보, 타입을 조회*/
/*2016.02.03
  아우터조인으로 처리하는 경우
  Failed to find conversion function from unknown to text
  에러가 발생함.. 그래서 with절을 이용, case문으로 pk여부를 체크함.
*/
with pktb as (
	SELECT C.COLUMN_NAME
	FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE C, INFORMATION_SCHEMA.TABLE_CONSTRAINTS S
	WHERE C.CONSTRAINT_NAME = S.CONSTRAINT_NAME
	AND S.CONSTRAINT_TYPE = 'PRIMARY KEY' AND C.TABLE_NAME = ':tableName'
	#if($databaseName)
	AND c.constraint_schema = :databaseName
	AND c.table_schema = :databaseName
	#end
)

SELECT
 B.COLUMN_NAME,
 case when ( select 1 from pktb a where a.column_name = b.column_name ) = 1 then 'PRI'
      else '' end COLUMN_KEY,
 B.IS_NULLABLE,
 B.ORDINAL_POSITION,
 B.DATA_TYPE,
 B.CHARACTER_MAXIMUM_LENGTH as DATA_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS AS B
	WHERE B.TABLE_NAME = :tableName
	#if($databaseName)
	AND B.TABLE_SCHEMA = :databaseName
	#end
	ORDER BY B.ORDINAL_POSITION






