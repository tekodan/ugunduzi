<?php
include_once "./../includes/init_database.php";
$dbh = initDB();

$df = fopen("php://output", 'w');
$query="SELECT user_alias, farm_name, farm_size_acres, internal_plot_id, plot_x, plot_y, plot_w, plot_h, plot_crop1, plot_crop2, plot_treatment1, plot_treatment2 FROM user, farm, plot WHERE farm.user_id = user.user_id AND plot.farm_id = farm.farm_id ORDER BY user_alias, farm_name, internal_plot_id";
$result = mysqli_query($dbh,$query);
while($row = mysqli_fetch_array($result,MYSQL_NUM)){
	fputcsv($df, $row);
}
fclose($df);

?>