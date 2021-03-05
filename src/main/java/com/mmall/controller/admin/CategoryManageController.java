package com.mmall.controller.admin;

import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(
            HttpSession session,
            String categoryName,
            @RequestParam(value = "parentId", defaultValue = 0)Integer parentId) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.NEED_LOGIN.getCode(),
                    "user does not login"
            );
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin user only: permission denied");
        }
        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping(value = "update_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateCategoryName(
            HttpSession session,
            String categoryName,
            Integer categoryId) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.NEED_LOGIN.getCode(),
                    "user does not login"
            );
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin user only: permission denied");
        }
        return iCategoryService.updateCategoryName(categoryName, categoryId);
    }

    @RequestMapping(value = "get_subcategory.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getSubCategoryWithoutRecursion(
            HttpSession session,
            @RequestParam(value = "categoryId", defaultValue = 0) Integer categoryId
    ) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.NEED_LOGIN.getCode(),
                    "user does not login"
            );
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin user only: permission denied");
        }
        return iCategoryService.getSubCategoryWithoutRecursion(categoryId);
    }

    @RequestMapping(value = "get_subcategory_recursion.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getSubCategoryWithRecursion(
            HttpSession session,
            @RequestParam(value = "categoryId", defaultValue = 0) Integer categoryId
    ) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.NEED_LOGIN.getCode(),
                    "user does not login"
            );
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin user only: permission denied");
        }
        return iCategoryService.getSubCategoryWithRecursion(categoryId);
    }

}
