package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    public ServerResponse addCategory(String name, Integer parentId);

    public ServerResponse updateCategoryName(String name, Integer id);

    public ServerResponse<List<Category>> getSubCategoryWithoutRecursion(Integer id);

    public ServerResponse<List<Integer>> getSubCategoryWithRecursion(Integer id);
}
