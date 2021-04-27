package com.mmall.common;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

public class Constants {
    public static final String CURRENT_USER = "currentUser";
    public static final String IMAGE_UPLOAD_DIR = "upload";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public static final String CACHE_TOKEN_PREFIX = "token_";

    public interface RedisCacheExpirationTime {
        int REDIS_SESSION_EXPIRATION_TIME = 60 * 30;
    }

    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public interface CartItemStatus {
        Integer SELECTED = 1;
        Integer UN_SELECTED = 0;
    }

    public interface CartLimit {
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    @AllArgsConstructor
    @Getter
    public enum ProductStatusEnum {
        ON_SALE(1, "ON_SALE");

        private int code;
        private String value;
    }

    @AllArgsConstructor
    @Getter
    public enum OrderStatusEnum {
        CANCELED(0, "canceled"),
        PENDING_PAYMENT(10, "pending payment"),
        PAID(20, "paid"),
        SHIPPED(40, "shipped"),
        SUCCESS(50, "order success"),
        CLOSED(60, "order closed");

        private int code;
        private String value;

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("cannot find enum");
        }
    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAYMENT = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    @AllArgsConstructor
    @Getter
    public enum PaymentMethod {
        ALIPAY(1, "Alipay");

        private int code;
        private String value;
    }

    @AllArgsConstructor
    @Getter
    public enum PaymentTypeEnum {
        ONLINE_PAYMENT(1, "ONLINE_PAYMENT");

        private int code;
        private String value;

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("cannot find enum");
        }
    }
}








