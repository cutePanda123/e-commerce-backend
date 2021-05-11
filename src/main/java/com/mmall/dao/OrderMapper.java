package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNumber(@Param("userId") Integer userId, @Param("orderNumber") Long orderNumber);

    Order selectByOrderNumber(Long orderNum);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAllOrders();

    List<Order> selectByStatusAndCreateTime(
            @Param("status") Integer status,
            @Param("date") String date
    );

    int closeOrderById(Integer id);
}