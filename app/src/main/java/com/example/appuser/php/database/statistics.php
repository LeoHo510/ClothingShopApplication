<?php
    include "connect.php";

    $query = "SELECT `idproduct`, product.name, product.url_img, product.price, product.info, product.inventory_quantity, product.category,
    SUM(`quantity`) AS Total
    FROM orderdetails
    INNER JOIN product ON product.id = orderdetails.idproduct
    GROUP BY `idproduct`
    HAVING Total > 5
    ORDER BY RAND() LIMIT 10";
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
            'message' => "Failed",
        ];
    }

    print_r(json_encode($arr));
?>