<?php
 

 if($_SERVER["REQUEST_METHOD"]=="POST"){
 	include 'login.php';
 	mostrarMensajesChats();
 }

 function mostrarMensajesChats(){
 	global $connect;

 	$chat_id = $_POST['chat_id'];

 

 	$query=" Select m.contenido, m.dia, m.telefono, u.nombre FROM mensajes m, usuarios u WHERE m.telefono=u.telefono and chat_id='$chat_id'; ";

 	$result = mysqli_query($connect, $query);
 	$number_of_rows=mysqli_num_rows($result);
 	$temp_array=array();

 	if($number_of_rows>0){
 		while ($row=mysqli_fetch_assoc($result)){
 			$temp_array[]=$row;
 		}
 	}

 	header('Content-Type: application/json');
 	echo json_encode(array("mensajes_del_chat"=>$temp_array));
 	mysqli_close($connect);
 }

?>