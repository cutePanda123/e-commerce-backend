package com.mmall.controller.portal;

import com.github.pagehelper.StringUtil;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisUtil;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> listCartItems(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.listCartItems(user.getId());
    }

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> addCartItems(HttpServletRequest request, Integer count, Integer productId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.addItem(user.getId(), productId, count);
    }

    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest request, Integer count, Integer productId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.updateCartItemQuantity(user.getId(), productId, count);
    }

    @RequestMapping(value = "select_all.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), null, Constants.CartItemStatus.SELECTED);
    }

    @RequestMapping(value = "unselect_all.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> unselectAll(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), null, Constants.CartItemStatus.UN_SELECTED);
    }

    @RequestMapping(value = "select.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest request, Integer productId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), productId, Constants.CartItemStatus.SELECTED);
    }

    @RequestMapping(value = "unselect.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> unselect(HttpServletRequest request, Integer productId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnselectCartItem(user.getId(), productId, Constants.CartItemStatus.UN_SELECTED);
    }

    @RequestMapping(value = "delete.do", method = RequestMethod.DELETE)
    @ResponseBody
    public ServerResponse<CartVo> delete(HttpServletRequest request, String productIds) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProducts(user.getId(), productIds);
    }

    @RequestMapping(value = "get_cart_items_quantity.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> getCartItemsTotalQuantity(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartItemsTotalQuantity(user.getId());
    }
}
