<?php

	$conn = mysqli_connect("localhost","root","","InstrumentDB");

	$id = $_POST["id"];
	$name = $_POST["name"];
	$type = $_POST["type"];
	
	$response = array();

	$statement = mysqli_prepare($conn,"UPDATE Instruments SET name = ? , type = ? WHERE id = ?");
	mysqli_stmt_bind_param($statement,"sss",$name,$type,$id);
	mysqli_stmt_execute($statement);
	
	$response["success"] = true;
	print(json_encode($response));

?>