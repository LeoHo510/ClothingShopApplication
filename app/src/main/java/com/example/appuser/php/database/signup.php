<?php
    include "connect.php";
    $password = $_POST["password"];
    $firstname = $_POST["firstname"];
    $lastname = $_POST["lastname"];
    $address = $_POST["address"];
    $phonenumber = $_POST["phonenumber"];
    $email = $_POST["email"];
    $uid = $_POST["uid"];

    $query1 = "SELECT * FROM `user` WHERE `email` = '$email' AND `password` = '$password'";
    $data1 = mysqli_query($conn,$query1);
    $result = array();
    while($row = mysqli_fetch_assoc($data1)) {
        $result[] = ($row);
    }
    if(empty($result)) {
        $query2 = "INSERT INTO `user`(`password`, `firstname`, `lastname`, `address`, `phonenumber`, `email`, `uid`) VALUES ('$password','$firstname','$lastname','$address','$phonenumber','$email', '$uid')";
        $data2 = mysqli_query($conn, $query2);

        if($data2) {
            $arr = [
                'success' => true,
                'message' => 'successful'
            ];
        } else {
            $arr = [
                'success' => false,
                'message' => 'failed'
            ];
        }
    } else {
        $arr = [
            'success' => false,
            'message' => "Accounts that already exist"
        ];
    }



    print_r(json_encode($arr));
?>