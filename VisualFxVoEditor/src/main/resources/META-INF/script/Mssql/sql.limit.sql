/*2016.02.11 startRow, maxRow 변수 추가*/
select 
#if($maxRow)
top $maxRow
#else
top 100
#end
a.* from ($usersql nolock) a 