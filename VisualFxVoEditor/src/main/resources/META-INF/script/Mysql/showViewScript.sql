select TABLE_NAME from information_schema.VIEWS
where 1=1
and TABLE_NAME like concat('%',:viewName,'%')
ORDER BY TABLE_NAME