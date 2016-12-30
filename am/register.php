<?php

	$conn = mysqli_connect("localhost","root","","InstrumentDB");

	$name = $_POST["name"];
	$username = $_POST["username"];
	$password = $_POST["password"];
	$email = $_POST["email"];
	
	$statement = mysqli_prepare($conn,"SELECT * FROM Users WHERE username = ?");
	mysqli_stmt_bind_param($statement,"s",$username);
	mysqli_stmt_execute($statement);
	
	$response = array();
	
	if(mysqli_stmt_fetch($statement)){
		
		$response["success"] = false;
		print(json_encode($response));
		
	} else {

		$statement = mysqli_prepare($conn,"INSERT INTO Users (name,email,username,password) VALUES (?,?,?,?)");
		mysqli_stmt_bind_param($statement,"ssss",$name,$email,$username,$password);
		mysqli_stmt_execute($statement);
		
		$response["success"] = true;
		print(json_encode($response));

	}
?>