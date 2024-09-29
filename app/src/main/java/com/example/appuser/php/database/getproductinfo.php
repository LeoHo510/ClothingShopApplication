<?php
    include "connect.php";
    $id = $_POST["id"];
    $query = "SELECT * FROM `product` WHERE `id` = '$id'";
    $data = mysqli_query($conn,$query);
    $result = array();
    while($row = mysqli_fetch_assoc($data)) {
        $result[] = ($row);
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
            'message' => "Failed"
        ];
    }
    print_r(json_encode($arr));
?>