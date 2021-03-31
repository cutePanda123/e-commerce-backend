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
}
