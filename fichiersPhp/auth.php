<?php
$hostname = "localhost";
$dbname = "u21408532";
$username = "u21408532";
$password = "c8cIA9tjWLCo";

$dsn = "mysql:host=$hostname;dbname=$dbname;charset=utf8";

$db = new PDO($dsn, $username, $password);
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
$db->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
?>