<?php
    include "connect.php";
    $inventory_quantity = $_POST["inventory_quantity"];
    $id = $_POST["id"];
    $query = "UPDATE `product` SET `inventory_quantity`='$inventory_quantity' WHERE `id` = '$id'";
    $data = mysqli_query($conn,$query);
    if($data) {
        $arr = [
            'success' => true,
            'message' => "Successful"
        ];
    } else {
        $arr = [
            'success' => false,
            'message' => "Failed"
        ];
    }
    print_r(json_encode($arr));
?>