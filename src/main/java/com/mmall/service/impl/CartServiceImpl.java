package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> addItem(Integer userId, Integer productId, Integer count) {
        if (userId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Constants.CartItemStatus.SELECTED.byteValue());
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        } else {
            cart.setQuantity(count + cart.getQuantity());
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return listCartItems(userId);
    }

    public ServerResponse<CartVo> updateCartItemQuantity(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            return ServerResponse.createByErrorMessage("cart cannot be found");
        }
        cart.setQuantity(count);
        cartMapper.updateByPrimaryKeySelective(cart);
        return listCartItems(userId);
    }

    public ServerResponse<CartVo> deleteProducts(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        cartMapper.deleteByUserIdAndProductIds(userId, productIdList);
        return listCartItems(userId);
    }

    public ServerResponse<CartVo> listCartItems(Integer userId) {
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> selectOrUnselectCartItem(Integer userId, Integer productId, Integer isSelected) {
        cartMapper.selectOrUnselectCartItems(userId, null, isSelected);
        return listCartItems(userId);
    }

    public ServerResponse<Integer> getCartItemsTotalQuantity(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartItemTotalQuantity(userId));
    }

    private CartVo getCartVo(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartItems = cartMapper.selectCartsByUserId(userId);
        List<CartProductVo> cartProductVos = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        for (Cart cartItem : cartItems) {
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setId(cartItem.getId());
            cartProductVo.setUserId(userId);
            cartProductVo.setProductId(cartItem.getProductId());

            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product == null) {
                continue;
            }
            cartProductVo.setProductMainImage(product.getMainImageUrl());
            cartProductVo.setProductName(product.getName());
            cartProductVo.setProductSubtitle(product.getSubtitle());
            cartProductVo.setProductStatus(product.getStatus());
            cartProductVo.setProductPrice(product.getPrice());
            cartProductVo.setProductStock(product.getStock());
            int limitCount = 0;
            if (product.getStock() >= cartItem.getQuantity()) {
                limitCount = cartItem.getQuantity();
                cartProductVo.setLimitQuantity(Constants.CartLimit.LIMIT_NUM_SUCCESS);
            } else {
                limitCount = product.getStock();
                cartProductVo.setLimitQuantity(Constants.CartLimit.LIMIT_NUM_FAIL);
                Cart cartItemWithUpdatedQuantity = new Cart();
                cartItemWithUpdatedQuantity.setQuantity(limitCount);
                cartItemWithUpdatedQuantity.setId(cartItem.getId());
                cartMapper.updateByPrimaryKeySelective(cartItemWithUpdatedQuantity);
            }
            cartProductVo.setQuantity(limitCount);
            cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
            cartProductVo.setProductChecked(cartItem.getChecked());

            if (cartItem.getChecked() == Constants.CartItemStatus.SELECTED.byteValue()) {
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
            }
            cartProductVos.add(cartProductVo);
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVos);
        cartVo.setAllChecked(areAllCartItemsSelected(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    private boolean areAllCartItemsSelected(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductSelectedStatusByUserId(userId) == 0;
    }
}
