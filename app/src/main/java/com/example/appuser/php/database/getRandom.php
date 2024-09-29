<?php
    include "connect.php";
    $query = "SELECT DISTINCT * FROM `product` HAVING inventory_quantity > 0 ORDER BY RAND() LIMIT 10";
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
    print_r(json_encode($arr));
?>