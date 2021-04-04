package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    public ServerResponse pay(Long orderNum, Integer userId, String paymentQrCodePath);

    public ServerResponse alipayHandler(Map<String, String> params);

    public ServerResponse queryOrderPaymentStatus(Integer userId, Long orderNum);

    public ServerResponse createOrder(Integer userId, Integer shippingId);
}
