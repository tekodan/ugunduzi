<?php
include_once "./../includes/init_database.php";
$dbh = initDB();

$df = fopen("php://output", 'w');
$query="SELECT treatment_id, treatment_name, treatment_category FROM treatment ORDER BY treatment_name";
$result = mysqli_query($dbh,$query);
while($row = mysqli_fetch_array($result,MYSQL_NUM)){
	fputcsv($df, $row);
}
fclose($df);

?>