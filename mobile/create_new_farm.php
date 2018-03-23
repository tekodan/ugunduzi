<?php
include_once "./../includes/init_database.php";
include_once "./../includes/functions.php";
$dbh = initDB();

if(isset($_GET['farm'])){
	$farm_string=$_GET['farm'];
	$farm_parts=explode(";",$farm_parts);
	if(sizeof($farm_parts)>=13){
		$alias=$farm_parts[0];
		$pass=$farm_parts[1];
		$user_id=getUserIDFromAlias($dbh,$alias);
		if($user_id==-1){
			//create new user
		}
		$farm_name=$farm_parts[2];
		// ...
		echo("ok");
	} else {
		echo("ko");
	}
	
} else {
	echo("ko");
}

?>