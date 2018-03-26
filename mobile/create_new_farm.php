<?php
include_once "./../includes/init_database.php";
include_once "./../includes/functions.php";
$dbh = initDB();

if(isset($_GET['farm'])){
	$farm_string=$_GET['farm'];
	$farm_parts=explode(";",$farm_string);
	if(sizeof($farm_parts)>=13){
		$alias=$farm_parts[0];
		$pass=$farm_parts[1];
		$user_id=getUserIDFromAlias($dbh,$alias);
		if($user_id==-1){
			$user_id=createNewUser($dbh,$alias,$pass);
			$output_part_1=$user_id;
		} else {
			$output_part_1="0";
		}
		$farm_name=str_replace("_"," ",$farm_parts[2]); 
		$farm_size=$farm_parts[3];
		$farm_id=getFarmIDFromNameUser($dbh,$farm_name,$user_id); // get farm id
		if($farm_id==-1){
			$farm_id=createNewFarm($dbh,$farm_name,$farm_size,$user_id,-1);
		} else {
			if(farmHasData($dbh,$farm_id)){
				$farm_id=createNewFarm($dbh,$farm_name,$farm_size,$user_id,$farm_id); //if farm exists AND has data, create a new farm with parent farm = previous farm (child farms invalidate parent farms)
			} else {
				deleteFarmPlots($dbh,$farm_id); //if farm exists but has no data, update plots
			}
		}
		$output_part_2=$farm_id;
		for($i=4;$i<sizeof($farm_parts);$i+=9){
			$plot_id=$farm_parts[$i];
			$plot_x=$farm_parts[$i+1];
			$plot_y=$farm_parts[$i+2];
			$plot_w=$farm_parts[$i+3];
			$plot_h=$farm_parts[$i+4];
			$plot_c1=$farm_parts[$i+5];
			$plot_c2=$farm_parts[$i+6];
			$plot_t1=$farm_parts[$i+7];
			$plot_t2=$farm_parts[$i+8];
			createNewPlot($dbh,$farm_id,$plot_id,$plot_x,$plot_y,$plot_w,$plot_h,$plot_c1,$plot_c2,$plot_t1,$plot_t2);
		}
		echo($output_part_1.",".$output_part_2);
	} else {
		echo("ko");
	}
	
} else {
	echo("ko");
}

?>