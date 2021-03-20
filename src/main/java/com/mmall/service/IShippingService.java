package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;

public interface IShippingService {
    public ServerResponse<Map> addShippingInfo(Integer userId, Shipping shipping);

    public ServerResponse<String> deleteShippingInfo(Integer userId, Integer shippingId);

    public ServerResponse<String> updateShippingInfo(Integer userId, Shipping shipping);

    public ServerResponse<Shipping> getShippingInfo(Integer userId, Integer shippingId);

    public ServerResponse<PageInfo> listShippingInfo(Integer userId, Integer pageNum, Integer pageSize);
}
