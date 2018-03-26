<?php
function getUserIDFromAlias($dbh,$alias){
	$ret=-1;
	$query="SELECT user_id FROM user WHERE user_alias='$alias'";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		$ret=$row[0];
	}
	return $ret;
}

function getUserPassFromId($dbh,$id){
	$ret="-1";
	$query="SELECT user_password FROM user WHERE user_id=$id";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		$ret=$row[0];
	}
	return $ret;
}

function createNewUser($dbh,$alias,$pass){
	$query="INSERT INTO user (user_alias, user_password) VALUES('$alias', '$pass')";
	$result = mysqli_query($dbh,$query);
	$id = mysqli_insert_id($dbh);
	return $id;
}

function getFarmIDFromNameUser($dbh,$farm_name,$user_id){
	$ret=-1;
	$query="SELECT farm_id FROM farm WHERE farm_name='$farm_name' AND user_id=$user_id";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		$ret=$row[0];
	}
	return $ret;
}

function createNewFarm($dbh,$farm_name,$farm_size,$user_id,$parent_id){
	$current_date=date('Y-m-d');
	$query="INSERT INTO farm (user_id, farm_name, farm_size_acres, farm_date_created, parent_farm_id) VALUES($user_id, '$farm_name', $farm_size, '$current_date', $parent_id)";
	$result = mysqli_query($dbh,$query);
	$id = mysqli_insert_id($dbh);
	return $id;
}

function farmHasData($dbh,$farm_id){
	$ret=false;
	$query="SELECT COUNT(log_id) FROM log,plot WHERE plot.farm_id=$farm_id AND log.plot_id=plot.plot_id";
	$result = mysqli_query($dbh,$query);
	if($row = mysqli_fetch_array($result,MYSQL_NUM)){
		if($row[0]>0){
			$ret=true;
		}
	}
	return $ret;
}

function deleteFarmPlots($dbh,$farm_id){
	$query="DELETE FROM plot WHERE farm_id=$farm_id";
	$result = mysqli_query($dbh,$query);
}

function createNewPlot($dbh,$farm_id,$plot_id,$plot_x,$plot_y,$plot_w,$plot_h,$plot_c1,$plot_c2,$plot_t1,$plot_t2){
	$query="INSERT INTO plot(internal_plot_id, farm_id, plot_x, plot_y, plot_w, plot_h, plot_crop1, plot_crop2, plot_treatment1, plot_treatment2) VALUES ($plot_id, $farm_id, $plot_x,$plot_y,$plot_w,$plot_h,$plot_c1,$plot_c2,$plot_t1,$plot_t2)";
	$result = mysqli_query($dbh,$query);
}
?>



