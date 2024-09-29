<?php
    include "connect.php";
    $iduser = $_POST["iduser"];
    $firstname = $_POST["firstname"];
    $lastname = $_POST["lastname"];
    $address = $_POST["address"];
    $phonenumber = $_POST["phonenumber"];
    $email = $_POST["email"];

    $query = "UPDATE `user` SET `firstname`='$firstname',`lastname`='$lastname',`address`='$address',`phonenumber`='$phonenumber',`email`='$phonenumber' WHERE `iduser` = '$iduser'";
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
    print_r(json_encode($data));
?>