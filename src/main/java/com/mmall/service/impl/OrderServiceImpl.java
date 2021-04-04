package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.Car;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import net.sf.jsqlparser.util.deparser.UpdateDeParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static AlipayTradeService tradeService;
    static {
        Configs.init("alipayinfo.properties");
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().setCharset("utf-8").build();
    }

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public ServerResponse<String> adminShipProducts(Long orderNo) {
        Order order = orderMapper.selectByOrderNumber(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("cannot find order");
        }
        if (order.getStatus() != Constants.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createByErrorMessage("only paid order can be shipped");
        }
        order.setStatus(Constants.OrderStatusEnum.SHIPPED.getCode());
        order.setShippedTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        return ServerResponse.createBySuccess("order shipped");
    }

    public ServerResponse<PageInfo> adminSearchOrders(Long orderNo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNumber(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("cannot find order");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = buildOrderVo(order, orderItemList);

        PageInfo pageInfo = new PageInfo(Lists.newArrayList(orderVo));
        pageInfo.setList(Lists.newArrayList(orderVo));
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<OrderVo> adminGetOrderDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNumber(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("cannot find order");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = buildOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse<PageInfo> adminGetOrderList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrders();
        List<OrderVo> orderVoList = buildOrderVoList(orderList, null);
        PageInfo pageInfo = new PageInfo(orderVoList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = buildOrderVoList(orderList, userId);
        PageInfo pageInfo = new PageInfo(orderVoList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNumber(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("cannot find order with order number");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        OrderVo orderVo = buildOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();

        List<Cart> cartList = cartMapper.selectCartsByUserId(userId);
        ServerResponse response = getCartOrderItems(userId, cartList);
        if (!response.isSuccess()) {
            return response;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)response.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal totalPrice = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(buildOrderItemVo(orderItem));
        }
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setTotalPrice(totalPrice);
        orderProductVo.setFtpServerHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return ServerResponse.createBySuccess(orderProductVo);
    }

    public ServerResponse cancelOrder(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNumber(userId, orderNo);

        if (order == null) {
            return ServerResponse.createByErrorMessage("user does not have this order");
        }
        if (order.getStatus() != Constants.OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            return ServerResponse.createByErrorMessage("paid order cannot be canceled");
        }
        Order canceledOrder = new Order();
        canceledOrder.setId(order.getId());
        canceledOrder.setStatus(Constants.OrderStatusEnum.CANCELED.getCode());

        int rowCount = orderMapper.updateByPrimaryKeySelective(canceledOrder);
        return rowCount > 0 ? ServerResponse.createBySuccess() : ServerResponse.createByError();
    }

    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartItemList = cartMapper.selectCartsByUserId(userId);
        ServerResponse<List<OrderItem>> response = this.getCartOrderItems(userId, cartItemList);
        if (!response.isSuccess()) {
            return response;
        }
        List<OrderItem> orderItemList = response.getData();
        BigDecimal totalPrice = getOrderTotalPrice(orderItemList);

        Order order = buildOrder(userId, shippingId, totalPrice);
        if (order == null) {
            return ServerResponse.createByErrorMessage("order creation failed");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("cart is empty");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        orderItemMapper.batchInsert(orderItemList);
        reduceStocks(orderItemList);
        cleanCart(cartItemList);

        OrderVo orderVo = buildOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();

        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Constants.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setShippingFee(order.getShippingFee());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Constants.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingAddressId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingAddressId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(buildShippingVo(shipping));
        }

        orderVo.setPaymentTime(order.getPaymentTime());
        orderVo.setShippedTime(order.getShippedTime());
        orderVo.setDeliveredTime(order.getDeliveredTime());
        orderVo.setClosedTime(order.getClosedTime());
        orderVo.setCreateTime(order.getCreateTime());

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = buildOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private OrderItemVo buildOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductUnitPrice(orderItem.getProductUnitPrice());
        orderItemVo.setProductImageUrl(orderItem.getProductImageUrl());
        orderItem.setQuantity(orderItem.getQuantity());
        orderItem.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(orderItem.getCreateTime());
        return orderItemVo;
    }

    private ShippingVo buildShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverState(shipping.getReceiverState());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverMobile(shipping.getReceiverZipcode());
        return shippingVo;
    }

    private void cleanCart(List<Cart> cartItemList) {
        for (Cart cartItem : cartItemList) {
            cartMapper.deleteByPrimaryKey(cartItem.getId());
        }
    }

    private void reduceStocks(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order buildOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        long orderNo = generateOrderNo();
        order.setOrderNo((int)orderNo);
        order.setStatus(Constants.OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setShippingFee(new BigDecimal("0"));
        order.setPaymentType(Integer.valueOf(Constants.PaymentTypeEnum.ONLINE_PAYMENT.getCode()).byteValue());
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingAddressId(shippingId);
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }

    private long generateOrderNo() {
        long time = System.currentTimeMillis();
        return time + (new Random().nextInt() % 10);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal total = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            total = BigDecimalUtil.add(total.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return total;
    }

    private ServerResponse<List<OrderItem>> getCartOrderItems(Integer userId, List<Cart> cartItemList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("cart is empty");
        }
        for (Cart cartItem : cartItemList) {
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product.getStatus() != Constants.ProductStatusEnum.ON_SALE.getCode()) {
                return ServerResponse.createByErrorMessage("product " + product.getName() + " is not in sale state");
            }
            if (cartItem.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("product " + product.getName() + " does not have enough stock");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getMainImageUrl());
            orderItem.setProductUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    public ServerResponse pay(Long orderNum, Integer userId, String paymentQrCodePath) {
        Map<String, String> results = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNumber(userId, orderNum);
        if (order == null) {
            return ServerResponse.createByErrorMessage("the user does not have this order");
        }
        results.put("orderNum", String.valueOf(order.getOrderNo()));

        return generatePaymentQrCode(order, userId, paymentQrCodePath, results);
    }

    // Details about each argument can be found in Alipay Official Demo https://opendocs.alipay.com/open/54/104506
    private ServerResponse generatePaymentQrCode(Order order, Integer userId, String path, Map<String, String> resultMap) {
        String outTradeNo = order.getOrderNo().toString();

        String subject = new StringBuffer().append("mmall QR code payment, order number: ").append(outTradeNo).toString();

        String totalAmount = order.getPayment().toString();

        String undiscountableAmount = "0";

        String sellerId = "";

        String body = new StringBuffer().append("order: ").append(outTradeNo).append(", total price: ").append(totalAmount).toString();

        String operatorId = "test_operator_id";

        String storeId = "test_store_id";

        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        String timeoutExpress = "120m";

        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(Long.valueOf(order.getOrderNo()), userId);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goodsDetail = GoodsDetail.newInstance(
                    orderItem.getProductId().toString(),
                    orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getProductUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity()
            );

            goodsDetailList.add(goodsDetail);
        }

        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))// need to config this callback url in Alipay account
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = OrderServiceImpl.tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("alipay order submit success");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                String qrCodeImagePath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrCodeImageFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrCodeImagePath);

                File ftpServerTargetFile = new File(path, qrCodeImageFileName);
                try {
                    FtpUtil.uploadFiles(Lists.newArrayList(ftpServerTargetFile));
                } catch (IOException e) {
                    logger.error("upload to ftp server error:", e);
                    e.printStackTrace();
                }
                logger.info("filePath:" + qrCodeImagePath);
                String qrCodeImageFtpServerURL = PropertiesUtil.getProperty("ftp.server.http.prefix") + ftpServerTargetFile.getName();
                resultMap.put("qrCodeImageUrl", qrCodeImageFtpServerURL);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                logger.error("alipay submit order failure");
                return ServerResponse.createByErrorMessage("alipay submit order failure");
            case UNKNOWN:
                logger.error("alipay server error, order status unknown");
                return ServerResponse.createByErrorMessage("alipay server error, order status unknown");
            default:
                logger.error("unsupported alipay order type");
                return ServerResponse.createByErrorMessage("unsupported alipay order type");
        }
    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerResponse alipayHandler(Map<String, String> params) {
        Long orderNum = Long.parseLong(params.get("out_trade_no"));
        String tradeNum = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNumber(orderNum);
        if (order == null) {
            return ServerResponse.createByErrorMessage("not associated order with this alipay callback");
        }
        if (order.getStatus() >= Constants.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createByErrorMessage("duplicate alipay callback");
        }
        if (Constants.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Constants.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPaymentPlatform(Integer.valueOf(Constants.PaymentMethod.ALIPAY.getCode()).byteValue());
        payInfo.setPlatformPaymentId(tradeNum);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPaymentStatus(Integer userId, Long orderNum) {
        Order order = orderMapper.selectByUserIdAndOrderNumber(userId, orderNum);
        if (order == null) {
            return ServerResponse.createByErrorMessage("user does not have this order");
        }
        if (order.getStatus() >= Constants.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    private List<OrderVo> buildOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<OrderItem> orderItemList = null;
            if (isAdminUser(userId)) {
                orderItemList = orderItemMapper.selectByOrderNo(Long.valueOf(order.getOrderNo()));
            } else {
                orderItemList = orderItemMapper.selectByOrderNoAndUserId(Long.valueOf(order.getOrderNo()), userId);
            }
            OrderVo orderVo = buildOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    private boolean isAdminUser(Integer userId) {
        return userId == null;
    }
}









