<?php
include_once "./../includes/init_database.php";
$dbh = initDB();

if(isset($_GET['alias']) && isset($_GET['pass'])){
	$alias=$_GET['alias'];
	$pass=$_GET['pass'];
	$query="SELECT user_id FROM user WHERE user_alias='$alias'";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		$id=$row[0];
		$query="SELECT user_password FROM user WHERE user_id=$id";
		$result = mysqli_query($dbh,$query);
		if($row = mysqli_fetch_array($result,MYSQL_NUM)){
			if($row[0]==$pass){
				echo($id);
			} else {
				echo(0);
			}
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