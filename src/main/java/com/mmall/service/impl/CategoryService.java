package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    public ServerResponse addCategory(String name, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(name)) {
            return ServerResponse.createByErrorMessage("add category wrong arguments");
        }

        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("add category success");
        }
        return ServerResponse.createByErrorMessage("add category failed");
    }

    public ServerResponse updateCategoryName(String name, Integer id) {
        if (id == null || StringUtils.isBlank(name)) {
            return ServerResponse.createByErrorMessage("update category wrong arguments");
        }
        Category category = new Category();
        category.setName(name);
        category.setId(id);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("update category success");
        }
        return ServerResponse.createByErrorMessage("update category failed");
    }
}
