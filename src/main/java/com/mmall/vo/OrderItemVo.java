package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItemVo {
    private Integer orderNo;

    private Integer productId;

    private BigDecimal productUnitPrice;

    private String productName;

    private String productImageUrl;

    private Integer quantity;

    private BigDecimal totalPrice;

    private Date createTime;

    private Date updateTime;
}
