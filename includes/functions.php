<?php
function getUserIDFromAlias($dbh,$name){
	$ret=-1;
	$query="SELECT user_id FROM user WHERE user_alias='$alias'";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		$ret=$row[0];
	}
	return $ret;
}

function getUserPassFromId($dbh,$id){
	$ret="-1";
	$query="SELECT user_password FROM user WHERE user_id=$id";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		$ret=$row[0];
	}
	return $ret;
}
?>