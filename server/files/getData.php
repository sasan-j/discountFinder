<?php
/*
require_once('Mysql.php');
var_dump(function_exists('mysql_connect'));

try {
    $db = new Mysql('jafarnej_df', 'F&rLrO~4QyTi', 'jafarnej_df', 'localhost');
    print('ALL PASSED'); //for debugging
} catch (DatabaseException $ex) {
    print('redirect to custom error page will go here');
    var_dump($ex);
}

*/


class foo_mysqli extends mysqli {
    public function __construct($host, $user, $pass, $db) {
        parent::__construct($host, $user, $pass, $db);

        if (mysqli_connect_error()) {
            die('Connect Error (' . mysqli_connect_errno() . ') '
                    . mysqli_connect_error());
        }
    }
}

		
// define variables and set to empty values
$item_name = $rate = $location_txt = $latitude = $longitude = $user_id = "";

if ($_SERVER["REQUEST_METHOD"] == "POST")
{
  $item_name = test_input($_POST["item_name"]);
  $rate = test_input($_POST["rate"]);
  $location_txt = test_input($_POST["location_txt"]);
  $latitude = test_input($_POST["latitude"]);
  $longitude = test_input($_POST["longitude"]);
  $user_id = test_input($_POST["user_id"]);

}

function test_input($data)
{
  //$data = trim($data);
  //$data = stripslashes($data);
  //$data = htmlspecialchars($data);
  return $data;
}


$db = new foo_mysqli('localhost', 'jafarnej_df', 'F&rLrO~4QyTi', 'jafarnej_df');

echo 'Success... ' . $db->host_info . "\n";
//INSERT INTO discounts (item_name, rate, location_txt, latitude, longitude, user_id ) VALUES ('_name', '4.5', 'location_txt', 'fdsgdfg', 'vfdgfds', '3534634');
mysqli_query($db,"INSERT INTO discounts (item_name, rate, location_txt, latitude, longitude, user_id ) VALUES ('$item_name', '$rate', '$location_txt', '$latitude', '$longitude', '$user_id')");

$db->close();



var_dump($_FILES);
var_dump($_POST);

echo "bye";

?>
