package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {
    public ServerResponse pay(Long orderNum, Integer userId, String paymentQrCodePath);

    public ServerResponse alipayHandler(Map<String, String> params);

    public ServerResponse queryOrderPaymentStatus(Integer userId, Long orderNum);

    public ServerResponse createOrder(Integer userId, Integer shippingId);

    public ServerResponse cancelOrder(Integer userId, Long orderNo);

    public ServerResponse getOrderCartProduct(Integer userId);

    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    public ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize);

    public ServerResponse<PageInfo> adminGetOrderList(Integer pageNum, Integer pageSize);

    public ServerResponse<OrderVo> adminGetOrderDetail(Long orderNo);

    // to do: will change to search by other factors
    public ServerResponse<PageInfo> adminSearchOrders(Long orderNo, Integer pageNum, Integer pageSize);

    public ServerResponse<String> adminShipProducts(Long orderNo);
}
