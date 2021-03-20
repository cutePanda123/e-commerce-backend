package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService  {
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Map> addShippingInfo(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount == 0) {
            return ServerResponse.createByErrorMessage("add shipping info failure");
        }
        Map result = Maps.newHashMap();
        result.put("shippingId", shipping.getId());
        return ServerResponse.createBySuccess("add shipping info success", result);
    }

    public ServerResponse<String> deleteShippingInfo(Integer userId, Integer shippingId) {
        int rowCount = shippingMapper.deleteByShippingIdAndUserId(shippingId, userId);
        if (rowCount == 0) {
            return ServerResponse.createByErrorMessage("delete shipping info failure");
        }
        return ServerResponse.createBySuccess("delete shipping info success");
    }

    public ServerResponse<String> updateShippingInfo(Integer userId, Shipping shipping) {
        shipping.setUserId(userId); // set user id to avoid shipping's user id is wrong
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount == 0) {
            return ServerResponse.createByErrorMessage("update shipping info failure");
        }
        Map result = Maps.newHashMap();
        result.put("shippingId", shipping.getId());
        return ServerResponse.createBySuccess("update shipping info success");
    }

    public ServerResponse<Shipping> getShippingInfo(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId, userId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("get shipping info failure");
        }
        return ServerResponse.createBySuccess("get shipping info success", shipping);
    }

    public ServerResponse<PageInfo> listShippingInfo(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippings);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
