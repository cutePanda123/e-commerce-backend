package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
    public ServerResponse<CartVo> addItem(Integer userId, Integer productId, Integer count);

    public ServerResponse<CartVo> updateCartItemQuantity(Integer userId, Integer productId, Integer count);

    public ServerResponse<CartVo> deleteProducts(Integer userId, String productIds);

    public ServerResponse<CartVo> listCartItems(Integer userId);

    public ServerResponse<CartVo> selectOrUnselectCartItem(Integer userId, Integer productId, Integer isSelected);

    public ServerResponse<Integer> getCartItemsTotalQuantity(Integer userId);
}
