package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service
public class CategoryService implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryService.class);

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

    public ServerResponse<List<Category>> getSubCategoryWithoutRecursion(Integer id) {
        List<Category> categories = categoryMapper.selectSubCategoryByParentId(id);
        if (CollectionUtils.isEmpty(categories)) {
            logger.info("found no sub categories");
        }
        return ServerResponse.createBySuccess(categories);
    }

    public ServerResponse<List<Integer>> getSubCategoryWithRecursion(Integer id) {
        Set<Category> categories = Sets.newHashSet();
        findSubCategory(id, categories);

        List<Integer> categoryIds = Lists.newArrayList();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }
        return ServerResponse.createBySuccess(categoryIds);
    }

    private Set<Category> findSubCategory(Integer categoryId, Set<Category> categories) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categories.add(category);
        }
        List<Category> subCategories = categoryMapper.selectSubCategoryByParentId(categoryId);
        for (Category sub : subCategories) {
            findSubCategory(sub.getId(), categories);
        }
        return categories;
    }
}
