package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Integer id;

    private Integer orderNo;

    private Integer userId;

    private Integer shippingAddressId;

    private BigDecimal payment;

    private Byte paymentType;

    private BigDecimal shippingFee;

    private Integer status;

    private Date paymentTime;

    private Date shippedTime;

    private Date deliveredTime;

    private Date closedTime;

    private Date createTime;

    private Date updateTime;
}