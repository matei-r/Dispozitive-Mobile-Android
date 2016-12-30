<?php

	$conn = mysqli_connect("localhost","root","","InstrumentDB");

	$ids = $_POST["ids"];
	
	$delete_ids = explode(";",$ids);
	
	foreach($delete_ids as $instr_id){
		$statement = mysqli_prepare($conn,"DELETE FROM Instruments WHERE id = ?");
		mysqli_stmt_bind_param($statement,"s",$instr_id);
		mysqli_stmt_execute($statement);
	}

	$response = array();
	
	$response["success"] = true;
	print(json_encode($response));

?>