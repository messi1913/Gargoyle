/*2017-06-28 by kyj  Primary key에 대한 부분을 넣는게 추가적으로 필요한데.. 
Primary key에 대한 display 값을 못찾겠음 */
SELECT
   'create table ' || B.SCHEMANAME || '.'|| A.TABLENAME  || '(' 
FROM
    SYS.SYSTABLES A, SYS.SYSSCHEMAS B
WHERE 1=1
AND A.TABLENAME = :tableName
AND B.SCHEMANAME = :databaseName
AND A.TABLETYPE = 'T'
AND A.SCHEMAID = B.SCHEMAID
union all




select c.COLUMNNAME || ' ' || c.columndatatype || ' '  ||  case when columndefault is null then '' else ' default ' || columndefault end ||
   (case when c.columnnumber = m.mvalue then  ''
   else ',' end )
FROM sys.sysschemas s, sys.systables t, sys.syscolumns c,  (
/*키 갯수*/
select count(1) mvalue
FROM sys.sysschemas s, sys.systables t, sys.syscolumns c
WHERE 1=1
and t.TABLEID = c.REFERENCEID 
and t.tablename = :tableName
and s.schemaname = :databaseName
and s.schemaid = t.schemaid

) m
WHERE t.TABLEID = c.REFERENCEID 
and s.schemaname = :databaseName
and t.tablename = :tableName
and s.schemaid = t.schemaid

union all

values ')'