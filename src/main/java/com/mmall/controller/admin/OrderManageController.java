package com.mmall.controller.admin;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listOrder(
            HttpSession session,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
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
        return iOrderService.adminGetOrderList(pageNum, pageSize);
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderVo> getOrderDetail(HttpSession session, Long orderNo) {
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
        return iOrderService.adminGetOrderDetail(orderNo);
    }

    @RequestMapping(value = "search.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> searchOrder(
            HttpSession session,
            Long orderNo,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
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
        return iOrderService.adminSearchOrders(orderNo, pageNum, pageSize);
    }

    @RequestMapping(value = "ship_products.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> shipProducts(HttpSession session, Long orderNo) {
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
        return iOrderService.adminShipProducts(orderNo);
    }
}
