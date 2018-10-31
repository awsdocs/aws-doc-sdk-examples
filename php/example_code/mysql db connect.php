<?php
$servername = "localhost";
$username = "uname";
$password = "pword";

$connection = new mysqli($servername, $username, $password);

// Check connection
if ($connection->connect_error) {
    die("couldnt connect: " . $connection->connect_error);
} 
echo "Connected";
?>
