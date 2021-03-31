package com.mmall.service.impl;

import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderMapper;
import com.mmall.pojo.Order;
import com.mmall.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    public ServerResponse pay(Long orderNum, Integer userId, String paymentQrCodePath) {
        Map results = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNumber(userId, orderNum);
        if (order == null) {
            return ServerResponse.createByErrorMessage("the user does not have this order");
        }
        results.put("orderNum", String.valueOf(order.getOrderNo()));
    }
}
