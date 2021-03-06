package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.net.Inet4Address;
import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectCartsByUserId(Integer userId);

    int selectCartProductSelectedStatusByUserId(Integer userId);

    int deleteByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productIds") List<String> productIds);

    int selectOrUnselectCartItems(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("isSelected") Integer isSelected);

    int selectCartItemTotalQuantity(@Param("userId") Integer userId);

    List<Cart> selectSelectedCartItemByUserId(Integer userid);
}