/*2016.02.11 startRow, maxRow 변수 추가*/
select a.* from ($usersql ) a
where 1=1
#if($startRow && $maxRow)
 and rownum between  $startRow and  $maxRow
#elseif($startRow)
 and rownum <= $startRow
#elseif($maxRow)
 and rownum <= $maxRow
#else
and rownum <= 100
#end