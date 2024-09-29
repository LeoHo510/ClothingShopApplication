<?php
    include "connect.php";

    $query = "SELECT `idproduct`, product.name, COUNT(`quantity`) as total FROM orderdetails INNER JOIN `product` ON product.id = orderdetails.idproduct GROUP BY `idproduct`";
    $data = mysqli_query($conn, $query);
    $result = array();
    while($row = mysqli_fetch_assoc($data)) {
        $result[] = ($row);
    }

    if(!empty($result)) {
        $arr = [
            'success' => true,
            'message' => 'Successfull',
            'result' => $result
        ];
    } else {
        $arr = [
            'success' => false,
            'message' => 'Failed',
            'result' => $result
        ];
    }

    print_r(json_encode($arr));
?>