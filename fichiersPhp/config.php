<?php

//header('Content-Type : application/json; charset=utf-8');

$hostname = "localhost";
$dbname = "u21408532";
$username = "u21408532";
$password = "c8cIA9tjWLCo";

$dsn = "mysql:host=$hostname;dbname=$dbname;charset=utf8";

$db = new PDO($dsn, $username, $password);
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
$db->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

$u = "sasi";
$p = "kumar";

$SQL = "SELECT * FROM user_list WHERE username = '$u' and password = '$p'";
    $res = $db->prepare($SQL);
    $res->execute();
    /*
    if($res->rowCount() === 0) echo json_encode("HEY");
    else echo json_encode("COUCOU");*/

function is_user_exist($user, $password){
	
    $SQL = "SELECT * FROM user_list WHERE username = '$user' and password = '$password'";
    global $db;
    $res = $db->prepare($SQL);
    $res->execute();
    
    if($res->rowCount() === 0) return false;
    else return true;
}


if(isset($_POST['username']) && isset($_POST['password'])){
    //echo "HELLO";
    $username = $_POST['username'];
    $password = $_POST['password'];
    if(is_user_exist($username, $password)){
        $json['success'] = 'Welcome '.$username;
    }else{ 
        $json['error'] = 'Error '.$username;
    }
    echo json_encode($json);
}

//echo "HELLO";
?>