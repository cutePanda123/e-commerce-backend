package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "pay.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(Long orderNum, HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath(Constants.IMAGE_UPLOAD_DIR);

        return iOrderService.pay(orderNum, user.getId(), path);
    }

    /*
        Two ways to test this alipay callback endpoint
        1. deploy the code to cloud vm, enable remote debug on tomcat server, and config remote debug in local IDEA
        2. enable local callback endpoint by using Natapp
     */
    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for (Iterator it = requestParams.keySet().iterator(); it.hasNext();) {
            String name = (String)it.next();
            String[] values = (String[]) requestParams.get(name);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < values.length; ++i) {
                buffer.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, buffer.toString());
        }
        logger.info("alipay callback: sign : {}, trade_status: {}, params: {}", params.get("sign"), params.get("trade+status"), params.toString());

        // verify callback data integrity
        params.remove("sign_type");
        try {
            boolean alipayRsaCheckV2Result = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            // ToDo: verify all the data in the callback is correct, for example, username, email, etc
            boolean verifyAllTheDataResult = true;
            if (!alipayRsaCheckV2Result || !verifyAllTheDataResult) {
                logger.error("invalid alipay result");
                return ServerResponse.createByErrorMessage("invalid alipay callback request");
            }
        } catch (AlipayApiException e) {
            logger.error("alipay callback exception", e);
            e.printStackTrace();
        }

        ServerResponse response = iOrderService.alipayHandler(params);
        if (response.isSuccess()) {
            return Constants.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Constants.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping(value = "query_order_payment_status.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse queryOrderPaymentStatus(HttpServletRequest request, Long orderNum) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = iOrderService.queryOrderPaymentStatus(user.getId(), orderNum);

        return response.isSuccess() ? ServerResponse.createBySuccess(true) : ServerResponse.createBySuccess(false);
    }

    @RequestMapping(value = "create.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpServletRequest request, Integer shippingId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.createOrder(user.getId(), shippingId);
    }

    @RequestMapping(value = "cancel.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest request, Long orderNo) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.cancelOrder(user.getId(), orderNo);
    }

    @RequestMapping(value = "get_order_cart_products.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse detail(HttpServletRequest request, Long orderNo) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse list(
            HttpServletRequest request,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }
}
