<?php
    include "connect.php";

    $iduser = $_POST["iduser"];
    $token = $_POST["token"];

    $query = "UPDATE `user` SET `token`='$token' WHERE `iduser` = '$iduser'";
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