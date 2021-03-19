package com.mmall.controller.portal;

import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> listCartItems(HttpSession session) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.listCartItems(user.getId());
    }

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> addCartItems(HttpSession session, Integer count, Integer productId) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.addItem(user.getId(), productId, count);
    }

    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.updateCartItemQuantity(user.getId(), productId, count);
    }

    @RequestMapping(value = "select_all.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), null, Constants.CartItemStatus.SELECTED);
    }

    @RequestMapping(value = "unselect_all.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> unselectAll(HttpSession session) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), null, Constants.CartItemStatus.UN_SELECTED);
    }

    @RequestMapping(value = "select.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), productId, Constants.CartItemStatus.SELECTED);
    }

    @RequestMapping(value = "unselect.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> unselect(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), productId, Constants.CartItemStatus.UN_SELECTED);
    }

    @RequestMapping(value = "delete.do", method = RequestMethod.DELETE)
    @ResponseBody
    public ServerResponse<CartVo> delete(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProducts(user.getId(), productIds);
    }

    @RequestMapping(value = "get_cart_items_quantity.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> getCartItemsTotalQuantity(HttpSession session) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartItemsTotalQuantity(user.getId());
    }
}
