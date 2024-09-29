<?php
    include "connect.php";
    $iduser = $_POST["iduser"];
    if($iduser == 0) {
        $query1 = "SELECT * FROM `orders`";
    } else {
        $query1 = "SELECT * FROM `orders` WHERE `iduser` = '$iduser'";
    }
    $data1 = mysqli_query($conn, $query1);
    $result = array();
    while($row = mysqli_fetch_assoc($data1)) {
        $orderId = $row['id'];  // Tách biến $row['id'] ra ngoài
        $query2 = "SELECT * FROM `orderdetails` INNER JOIN `product` ON orderdetails.idproduct = product.id WHERE orderdetails.idorder = '$orderId'";
        $data2 = mysqli_query($conn, $query2);
        $items = array();
        while($item = mysqli_fetch_assoc($data2)) {
            $items[] = $item;  // Sử dụng mảng để lưu trữ các sản phẩm
        }
        $row['items'] = $items;  // Gán mảng items vào $row['items']
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
            'message' => "Failed"
        ];
    }

    print_r(json_encode($arr));
?>
