<?php
include_once "./../includes/init_database.php";
include_once "./../includes/functions.php";
$dbh = initDB();

if(isset($_GET['alias']) && isset($_GET['pass'])){
	$alias=$_GET['alias'];
	$pass=$_GET['pass'];
	$id=getUserIDFromAlias($dbh,$alias);
	if($id != -1){
		$real_pass=getUserPassFromId($dbh,$id);
		if($real_pass==$pass){
			echo($id);
		} else {
			echo(0);
		}
	} else {
		$query="INSERT INTO user (user_alias, user_password) VALUES('$alias', '$pass')";
		$result = mysqli_query($dbh,$query);
		$id = mysqli_insert_id($dbh);
		$id*=-1;
		echo($id);
	}
}

?>