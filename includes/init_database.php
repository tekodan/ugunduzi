<?
//initialize dbConnection
function initDB() {
	$host="localhost";
	$db="database_name";
	$db_user="user_name";
	$db_pass="user_password";
	$dbh=mysqli_connect($host, $db_user, $db_pass, $db);
	return $dbh;
}

//
?>
