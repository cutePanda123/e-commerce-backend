package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface IOrderService {
    public ServerResponse pay(Long orderNum, Integer userId, String paymentQrCodePath)
}
