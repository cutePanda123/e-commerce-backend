package com.mmall.service;

import com.mmall.common.ServerResponse;

public interface ICategoryService {
    public ServerResponse addCategory(String name, Integer parentId);

    public ServerResponse updateCategoryName(String name, Integer id);
}
