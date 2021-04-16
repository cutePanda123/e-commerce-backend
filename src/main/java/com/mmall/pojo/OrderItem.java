package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Integer id;

    private Integer orderNo;

    private Integer userId;

    private Integer productId;

    private BigDecimal productUnitPrice;

    private String productName;

    private String productImageUrl;

    private Integer quantity;

    private BigDecimal totalPrice;

    private Date createTime;

    private Date updateTime;
}