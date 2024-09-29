<?php
    include "connect.php";
    $name = $_POST["name"];
    $url_img = $_POST["url_img"];
    $price = $_POST["price"];
    $info = $_POST["info"];
    $inventory_quantity = $_POST["inventory_quantity"];
    $category = $_POST["category"];

    $query = "INSERT INTO `product`(`name`, `url_img`, `price`, `info`, `inventory_quantity`, `category`) VALUES ('$name','$url_img','$price','$info','$inventory_quantity','$category')";
    $data = mysqli_query($conn,$query);
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