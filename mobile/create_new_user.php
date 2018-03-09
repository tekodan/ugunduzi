<?php
include_once "./../includes/init_database.php";
$dbh = initDB();

if(isset($_GET['alias']) && isset($_GET['pass'])){
	$alias=$_GET['alias'];
	$pass=$_GET['pass'];
	$query="SELECT user_id FROM user WHERE user_alias='$alias' AND user_password='$pass'";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		echo($row[0]);
	} else {
		$query="INSERT INTO user (user_alias, user_password) VALUES('$alias', '$pass')";
		$result = mysqli_query($dbh,$query);
		$id = mysqli_insert_id($dbh);
		echo($id);
	}
}

?>