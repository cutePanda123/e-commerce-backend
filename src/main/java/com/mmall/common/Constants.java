package com.mmall.common;

public class Constants {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public static final String CACHE_TOKEN_PREFIX = "token_";

    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
}
