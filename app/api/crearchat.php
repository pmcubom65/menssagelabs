<?php 

if($_SERVER["REQUEST_METHOD"]=="POST"){
	require 'login.php';
	empezarChat();
}


function empezarChat(){
	global $connect;

	$chat_id = $_POST["chat_id"];
	$inicio=$_POST["inicio"];

	$query=" Insert into chats(chat_id, inicio) values ('$chat_id', '$inicio');";

	mysqli_query($connect, $query) or die (mysqli_error($connect));
	mysqli_close($connect);
}




?>