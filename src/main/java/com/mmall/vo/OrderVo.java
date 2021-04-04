package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {
    private Integer orderNo;

    private BigDecimal payment;

    private Byte paymentType;

    private String paymentTypeDesc;

    private BigDecimal shippingFee;

    private Integer status;

    private String statusDesc;

    private Date paymentTime;

    private Date shippedTime;

    private Date deliveredTime;

    private Date closedTime;

    private Date createTime;

    private Date updateTime;

    private List<OrderItemVo> orderItemVoList;

    private String imageHost;
    private String receiverName;
    private Integer shippingId;

    private ShippingVo shippingVo;
}

