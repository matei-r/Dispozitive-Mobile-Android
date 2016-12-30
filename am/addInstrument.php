<?php

	$conn = mysqli_connect("localhost","root","","InstrumentDB");

	$user_id = $_POST["id"];
	$name = $_POST["name"];
	$type = $_POST["type"];
	
	$response = array();

	$statement = mysqli_prepare($conn,"INSERT INTO Instruments (name,type,user_id) VALUES (?,?,?)");
	mysqli_stmt_bind_param($statement,"sss",$name,$type,$user_id);
	mysqli_stmt_execute($statement);
	
	$response["success"] = true;
	print(json_encode($response));

?>