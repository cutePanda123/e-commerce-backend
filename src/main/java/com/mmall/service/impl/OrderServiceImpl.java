package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

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
}
