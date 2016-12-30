<?php

	$conn = mysqli_connect("localhost","root","","InstrumentDB");

	$username = $_POST["username"];
	$password = $_POST["password"];
	
	$statement = mysqli_prepare($conn,"SELECT * FROM Users WHERE username = ? AND password = ?");
	mysqli_stmt_bind_param($statement,"ss",$username,$password);
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement,$id,$name,$email,$username,$password);
	
	$response = array();
	$response["success"] = false;
	
	while(mysqli_stmt_fetch($statement)){
		$response["success"] = true;
		$response["id"] = $id;
		$response["name"] = $name;
		$response["email"] = $email;
		$response["username"] = $username;
	}
	
	print(json_encode($response));

?>