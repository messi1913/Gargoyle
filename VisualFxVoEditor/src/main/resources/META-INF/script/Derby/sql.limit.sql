/*2016.02.11 최초작성 및 startRow maxRow 변수 추가.*/
select a.* from ($usersql ) a
#if($startRow && $maxRow)
offset $startRow row
fetch FIRST $maxRow row only
#elseif($startRow)
offset $startRow row
#elseif($maxRow)
fetch FIRST $maxRow row only
#else
fetch FIRST 100 row only
#end
