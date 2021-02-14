package com.mmall.pojo;

import java.util.Date;

public class PayInfo {
    private Integer id;

    private Integer userId;

    private Integer orderNo;

    private Byte paymentPlatform;

    private String platformPaymentId;

    private String platformStatus;

    private Date createTime;

    private Date updateTime;

    public PayInfo(Integer id, Integer userId, Integer orderNo, Byte paymentPlatform, String platformPaymentId, String platformStatus, Date createTime, Date updateTime) {
        this.id = id;
        this.userId = userId;
        this.orderNo = orderNo;
        this.paymentPlatform = paymentPlatform;
        this.platformPaymentId = platformPaymentId;
        this.platformStatus = platformStatus;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public PayInfo() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Byte getPaymentPlatform() {
        return paymentPlatform;
    }

    public void setPaymentPlatform(Byte paymentPlatform) {
        this.paymentPlatform = paymentPlatform;
    }

    public String getPlatformPaymentId() {
        return platformPaymentId;
    }

    public void setPlatformPaymentId(String platformPaymentId) {
        this.platformPaymentId = platformPaymentId == null ? null : platformPaymentId.trim();
    }

    public String getPlatformStatus() {
        return platformStatus;
    }

    public void setPlatformStatus(String platformStatus) {
        this.platformStatus = platformStatus == null ? null : platformStatus.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}