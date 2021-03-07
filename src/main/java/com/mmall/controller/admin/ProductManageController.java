package com.mmall.controller.admin;

import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.rmi.server.ServerCloneException;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    IUserService iUserService;

    @Autowired
    IProductService iProductService;

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.addOrUpdateProduct(product);
    }

    @RequestMapping(value = "update_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.updateSaleStatus(productId, status);
    }

    @RequestMapping(value = "get_product_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse manageProductDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.getProductDetail(productId);
    }


}
