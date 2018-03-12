<?php
include_once "./../includes/init_database.php";
$dbh = initDB();

$df = fopen("php://output", 'w');
$query="SELECT crop_id, crop_name, crop_variety FROM crop ORDER BY crop_name, crop_variety";
$result = mysqli_query($dbh,$query);
while($row = mysqli_fetch_array($result,MYSQL_NUM)){
	fputcsv($df, $row);
}
fclose($df);

?>