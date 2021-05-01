package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    IShippingService iShippingService;

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map> add(HttpServletRequest request, Shipping shipping) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.addShippingInfo(user.getId(), shipping);
    }

    @RequestMapping(value = "delete.do", method = RequestMethod.DELETE)
    @ResponseBody
    public ServerResponse<String> delete(HttpServletRequest request, Integer shippingId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.deleteShippingInfo(user.getId(), shippingId);
    }

    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> update(HttpServletRequest request, Shipping shipping) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.updateShippingInfo(user.getId(), shipping);
    }

    @RequestMapping(value = "get.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Shipping> get(HttpServletRequest request, Integer shippingId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.getShippingInfo(user.getId(), shippingId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest request)
    {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.listShippingInfo(user.getId(), pageNum, pageSize);
    }
}
