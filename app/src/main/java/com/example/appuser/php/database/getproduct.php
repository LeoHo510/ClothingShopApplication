<?php
    include "connect.php";
    $status = $_POST["status"];

    if($status == 1) {
        $query = "SELECT * FROM `product`";
        $data = mysqli_query($conn, $query);
        $result = array();
        while($row = mysqli_fetch_assoc($data)) {
            $result[] = ($row);
        }
        if(!empty($result) || $data == true) {
            $arr = [
                'success' => true,
                'message' => "success",
                'result' => $result
            ];
        } else {
            $arr = [
                'success' => false,
                'message' => "fail",
                'result' => $result
            ];
        }
    } else {
        $category = $_POST["category"];
        $query = "SELECT * FROM `product` WHERE `category` = '$category'";
        $data = mysqli_query($conn, $query);
        $result = array();
        while($row = mysqli_fetch_assoc($data)) {
            $result[] = ($row);
        }
        if(!empty($result) || $data == true) {
            $arr = [
                'success' => true,
                'message' => "success",
                'result' => $result
            ];
        } else {
            $arr = [
                'success' => false,
                'message' => "fail",
                'result' => $result
            ];
        }
    }
    print_r(json_encode($arr));
?>