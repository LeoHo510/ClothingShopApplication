<?php
    include "connect.php";
    $status = $_POST["status"];

    $query = "SELECT * FROM `user` WHERE `status` = $status";
    $data = mysqli_query($conn,$query);
    $result = array();
    while($row = mysqli_fetch_assoc($data)) {
        $result[] = ($row);
    }
    if(!empty($result)) {
        $arr = [
            'success' => true,
            'message' => "successful",
            'result' => $result
        ];
    } else {
        $arr = [
            'success' => false,
            'message' => "failed"
        ];
    }

    print_r(json_encode($arr));
?>