package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayInfo {
    private Integer id;

    private Integer userId;

    private Integer orderNo;

    private Byte paymentPlatform;

    private String platformPaymentId;

    private String platformStatus;

    private Date createTime;

    private Date updateTime;
}