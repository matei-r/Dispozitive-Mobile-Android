<?php

	$conn = mysqli_connect("localhost","root","","InstrumentDB");

	$id = $_GET["id"];
	
	$statement = mysqli_prepare($conn,"SELECT * FROM Instruments WHERE user_id = ?");
	mysqli_stmt_bind_param($statement,"i",$id);
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement,$id,$name,$type,$user_id);
	
	$response = array();
	$data = array();
	
	while(mysqli_stmt_fetch($statement)){
		$response["name"] = $name;
		$response["type"] = $type;
		$data[] = $response;
	}
	if($statement != null){
		print(json_encode($data));
	} else {
		print(json_encode([]));
	}

?>