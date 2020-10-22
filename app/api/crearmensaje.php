<?php 

if($_SERVER["REQUEST_METHOD"]=="POST"){
	require 'login.php';
	nuevomensaje();
}


function nuevomensaje(){
	global $connect;

	$chat_id = $_POST["chat_id"];
	$contenido=$_POST["contenido"];
	$dia=$_POST["dia"];
	$mensaje_id = $_POST["mensaje_id"];

	$telefono = $_POST["telefono"];

	$query=" Insert into mensajes(chat_id, contenido, dia, mensaje_id, telefono) values 
	('$chat_id', '$contenido', '$dia','$mensaje_id','$telefono');";

	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
}




?>