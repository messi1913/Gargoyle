SELECT name, sql FROM sqlite_master
WHERE type='table'
and name = ':tableName'
ORDER BY name;
