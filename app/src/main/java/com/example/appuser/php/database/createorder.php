<?php
    include "connect.php";

    // Lấy dữ liệu từ POST
    $firstname = $_POST["firstname"];
    $lastname = $_POST["lastname"];
    $address = $_POST["address"];
    $phonenumber = $_POST["phonenumber"];
    $email = $_POST["email"];
    $iduser = $_POST["iduser"];
    $quantity = $_POST["quantity"];
    $totalprice = $_POST["totalprice"];
    $details = $_POST["details"];

    // Chuẩn bị và thực thi câu lệnh cho bảng orders
    $stmt = $conn->prepare("INSERT INTO `orders`(`firstname`, `lastname`, `address`, `phonenumber`, `email`, `iduser`, `quantity`, `totalprice`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssssii", $firstname, $lastname, $address, $phonenumber, $email, $iduser, $quantity, $totalprice);

    if ($stmt->execute()) {
        $query = "SELECT `id` AS `idorder` FROM `orders` WHERE `iduser` = ? ORDER BY id DESC LIMIT 1";
        $stmt = mysqli_prepare($conn, $query);
        mysqli_stmt_bind_param($stmt, "i", $iduser);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        $row = mysqli_fetch_assoc($result);
        $idorder = $row['idorder'];

        if (!empty($idorder)) {
            $details = json_decode($details, true);
            $stmt_detail = $conn->prepare("INSERT INTO `orderdetails`(`idorder`, `idproduct`, `quantity`, `size`, `price`) VALUES (?, ?, ?, ?, ?)");
            $stmt_detail->bind_param("iiiid", $idorder, $idproduct, $item_quantity, $size, $item_price);

            foreach ($details as $item) {
                $idproduct = $item["id"];
                $item_quantity = $item["quantity"];
                $item_price = $item["price"];
                $size = $item["size"];

                if (!$stmt_detail->execute()) {
                    $arr = [
                        'success' => false,
                        'message' => "Failed to insert order detail"
                    ];
                    print_r(json_encode($arr));
                    exit();
                }
            }

            $arr = [
                'success' => true,
                'message' => "Successful"
            ];
        } else {
            $arr = [
                'success' => false,
                'message' => "Failed to retrieve order ID"
            ];
        }

        // Đóng statement
        mysqli_stmt_close($stmt_detail);
    } else {
        $arr = [
            'success' => false,
            'message' => "Failed to insert order"
        ];
    }

    // Đóng statement
    mysqli_stmt_close($stmt);

    // Đóng kết nối
    mysqli_close($conn);

    print_r(json_encode($arr));
?>