<?php
include "connect.php";

$key = $_POST["key"] ?? '';

if(empty($key)) {
    $arr = [
        'success' => false,
        'message' => "Key is empty"
    ];
} else {
    $query = "SELECT * FROM `product` WHERE `name` LIKE '%$key%'";
    $data = mysqli_query($conn, $query);
    $result = array();
    while($row = mysqli_fetch_assoc($data)) {
        $result[] = $row;
    }

    if(!empty($result)) {
        $arr = [
            'success' => true,
            'message' => "Successful",
            'result' => $result
        ];
    } else {
        $arr = [
            'success' => false,
            'message' => "No products found"
        ];
    }
}
echo json_encode($arr);
?>