package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
    public ServerResponse<CartVo> addItem(Integer userId, Integer productId, Integer count);

    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    public ServerResponse<CartVo> deleteProducts(Integer userId, String productIds);
}
