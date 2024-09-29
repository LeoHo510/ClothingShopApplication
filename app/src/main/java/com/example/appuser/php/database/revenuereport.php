<?php
    include "connect.php";
    $query = "SELECT *, SUM(`totalprice`) AS RevenueSales, MONTH(`date`) AS `month` FROM `orders` GROUP BY YEAR(`date`), MONTH(`date`)";
    $data = mysqli_query($conn,$query);
    $result = array();
    while($row = mysqli_fetch_assoc($data)) {
        $result[] = ($row);
    }
    if(!empty($result)) {
        $arr = [
            'success' => true,
            'message' => "Successfull",
            'result' => $result
        ];
    } else {
        $arr = [
            'success' => false,
            'message' => "Failed",
            'result' => $result
        ];
    }
    print_r(json_encode($arr));
?>