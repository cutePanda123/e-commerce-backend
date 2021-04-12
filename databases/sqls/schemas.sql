CREATE USER 'username' IDENTIFIED BY 'password';
CREATE DATABASE `mmall` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL PRIVILEGES ON *.* to 'username'@'%';
ALTER USER 'username'@'%' IDENTIFIED BY '';

FLUSH PRIVILEGES;

CREATE TABLE mmall_user (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT 'user id',
    username varchar(50) NOT NULL COMMENT 'username',
    password varchar(50) NOT NULL COMMENT 'user password encrypted with MD5',
    email varchar(50) NOT NULL DEFAULT '',
    phone varchar(20) NOT NULL DEFAULt '',
    security_question varchar(100) NOT NULL DEFAULT '' COMMENT 'security question for password recovery',
    security_answer varchar(100) NOT NULL DEFAULT '' COMMENT 'security answer for password recovery',
    role int(4) NOT NULL DEFAULT 1 COMMENT '0:admin, 1:user',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id),
    UNIQUE KEY user_name_unique (username) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARACTER SET=utf8;

CREATE TABLE `mmall_category` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'category id',
    `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT 'if parent_id == 0, this is a root category',
    `name` varchar(50) NOT NULL DEFAULT '' COMMENT 'category name',
    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1: normal, 2:deprecated',
    `sort_order` int(4) NOT NULL DEFAULT 0 COMMENT 'sort order: if equal, sort by based on creation time',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=100032 DEFAULT CHARACTER SET=utf8;

CREATE TABLE `mmall_product` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'product id',
    `category_id` int(11) NOT NULL DEFAULT 0 COMMENT 'foreign key to category table',
    `name` varchar(100) NOT NULL DEFAULT '' COMMENT 'product name',
    `subtitle` varchar(200) NOT NULL DEFAULT '' COMMENT 'product subtitle',
    `main_image_url` varchar(500) NOT NULL DEFAULT '' COMMENT 'main image (relative path)',
    `sub_images` text COMMENT 'image urls, json format',
    `detail` text COMMENT 'product details',
    `price` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT 'price',
    `stock` int(11) NOT NULL DEFAULT 0 COMMENT 'stock',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '1:in sale, 2:sold out, 3:deleted',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARACTER SET=utf8;

CREATE TABLE `mmall_cart` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL DEFAULT 0,
    `product_id` int(11) NOT NULL DEFAULT 0,
    `quantity` int(11) NOT NULL DEFAULT 0,
    `checked` tinyint NOT NULL DEFAULT 0 COMMENT '1:checked, 0:unchecked',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id),
    KEY `user_id_index` (`user_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARACTER SET=utf8;

CREATE TABLE `mmall_payment` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL DEFAULT 0,
    `order_id` int(11) NOT NULL DEFAULT 0,
    `payment_platform` tinyint NOT NULL DEFAULT 0 COMMENT '1:Alipay, 2:Wechat, 3:PayPal',
    `platform_payment_id` varchar(200) NOT NULL DEFAULT '' COMMENT 'order id from payment platform',
    `platform_status` varchar(200) NOT NULL DEFAULT '' COMMENT 'order status from payment platform',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARACTER SET=utf8;

CREATE TABLE `mmall_order` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `order_no` int(11) NOT NULL DEFAULT 0 COMMENT 'order number',
    `user_id` int(11) NOT NULL DEFAULT 0,
    `shipping_address_id` int(11) NOT NULL DEFAULT 0,
    `payment` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT 'order payment dollar amount',
    `payment_type` tinyint NOT NULL DEFAULT 0 COMMENT '1: online payment',
    `shipping_fee` decimal(20, 2) NOT NULL DEFAULT 0.00,
    `status` int(10) NOT NULL DEFAULT 10 COMMENT '0:cancelled, 10:unpaid, 20:paid, 40:shipped, 50:received, 60:closed',
    `payment_time` datetime DEFAULT NULL,
    `shipped_time` datetime DEFAULT NULL,
    `delivered_time` datetime DEFAULT NULL,
    `closed_time` datetime DEFAULT NULL,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id),
    UNIQUE KEY `order_no_index` (`order_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARACTER SET=utf8;

CREATE TABLE `mmall_order_detail` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `order_no` int(11) NOT NULL DEFAULT 0 COMMENT 'order number',
    `user_id` int(11) NOT NULL DEFAULT 0,
    `product_id` int(11) NOT NULL DEFAULT 0,
    `product_unit_price` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT 'product unit price when order created',
    `product_name` varchar(100) NOT NULL DEFAULT '',
    `product_image_url` varchar(500) NOT NULL DEFAULT '',
    `quantity` int(10) NOT NULL DEFAULT 0,
    `total_price` decimal(20, 2) NOT NULL DEFAULT 0.00,
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id),
    UNIQUE KEY `order_no_index` (`order_no`) USING HASH,
    UNIQUE KEY `order_no_user_id_index` (`order_no`, `user_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARACTER SET=utf8;

#ALTER TABLE mmall_order_detail DROP INDEX order_no_index;
#ALTER TABLE mmall_order_detail DROP INDEX order_no_user_id_index;

CREATE TABLE `mmall_shipping_address` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL DEFAULT 0,
    `receiver_name` varchar(20) NOT NULL DEFAULT '',
    `receiver_phone` varchar(20) NOT NULL DEFAULT '',
    `receiver_mobile` varchar(20) NOT NULL DEFAULT '',
    `receiver_state` varchar(20) NOT NULL DEFAULT '',
    `receiver_city` varchar(20) NOT NULL DEFAULT '',
    `receiver_address` varchar(200) NOT NULL DEFAULT '',
    `receiver_zipcode` varchar(10) NOT NULL DEFAULT '',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'last updated time',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARACTER SET=utf8;

ALTER TABLE mmall_cart DROP FOREIGN KEY mmall_cart_ibfk_1;
ALTER TABLE mmall_cart DROP FOREIGN KEY mmall_cart_ibfk_2;
ALTER TABLE mmall_payment CHANGE order_id order_no int(11);

ALTER TABLE mmall_category AUTO_INCREMENT = 10032;