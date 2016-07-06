/*2016.02.11 최초작성 및 startRow maxRow 변수 추가.*/
select * from (
select a.* from ($usersql ) a
 ) a
#if($startRow && $maxRow)
 limit $maxRow offset $startRow
#elseif($startRow)
 limit $startRow
#elseif($maxRow)
 limit $maxRow
#else
 limit 100
#end

