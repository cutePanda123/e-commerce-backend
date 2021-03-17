package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICartService {
    ServerResponse add(Integer userId, Integer productId, Integer count);
}
