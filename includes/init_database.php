<?
//initialize dbConnection
function initDB() {
	$host="localhost";
	$db="your_database";
	$db_user="your_user";
	$db_pass="your_password";
	$dbh=mysqli_connect($host, $db_user, $db_pass, $db);
	return $dbh;
}

//
?>
