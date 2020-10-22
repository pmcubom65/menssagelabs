<?php 

if($_SERVER["REQUEST_METHOD"]=="POST"){
	require 'login.php';
	nuevousuario();
}


function nuevousuario(){
	global $connect;

	$nombre = $_POST["nombre"];
	$telefono=$_POST["telefono"];
	$token=$_POST["token"];

	$query=" Insert into usuarios(nombre, telefono, token) values ('$nombre', '$telefono', '$token');";

	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
}




?>