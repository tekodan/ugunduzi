<?php
include_once "./../includes/init_database.php";
$dbh = initDB();

$df = fopen("php://output", 'w');
$query="SELECT units_id, units_name, units_type FROM units ORDER BY units_name";
$result = mysqli_query($dbh,$query);
while($row = mysqli_fetch_array($result,MYSQL_NUM)){
	fputcsv($df, $row);
}
fclose($df);

?>