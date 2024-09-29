-- Tạo bảng categoryadmin
CREATE TABLE categoryadmin (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    name TEXT NOT NULL
);

-- Tạo bảng orders
CREATE TABLE orders (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    phonenumber TEXT NOT NULL,
    email TEXT NOT NULL,
    iduser INT(10) NOT NULL,
    date DATE NOT NULL,
    quantity INT(11) NOT NULL,
    totalprice TEXT NOT NULL
);

-- Tạo bảng product
CREATE TABLE product (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url_image TEXT NOT NULL,
    price VARCHAR(20) NOT NULL,
    info TEXT NOT NULL,
    inventory_quantity INT(10) NOT NULL,
    category VARCHAR(255) NOT NULL
);

-- Tạo bảng user
CREATE TABLE user (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    password TEXT NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    phonenumber TEXT NOT NULL,
    email TEXT NOT NULL,
    uid TEXT NOT NULL,
    token TEXT NOT NULL,
    status INT(11) NOT NULL
);

-- Tạo bảng orderdetails
CREATE TABLE orderdetails (
    idorder INT(11) NOT NULL,
    idproduct INT(11) NOT NULL,
    quantity INT(11) NOT NULL,
    size INT(11) NOT NULL,
    price TEXT NOT NULL,
    FOREIGN KEY (idorder) REFERENCES orders(id),
    FOREIGN KEY (idproduct) REFERENCES product(id)
);

-- Tạo bảng sales
CREATE TABLE sales (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    url TEXT NOT NULL,
    info TEXT NOT NULL
);

-- Tạo bảng titleproduct
CREATE TABLE titleproduct (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL
);
