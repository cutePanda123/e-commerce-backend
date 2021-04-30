package com.mmall.util;

import com.github.pagehelper.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".panda.com";
    private final static String COOKIE_NAME = "JSESSIONID";

    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath(";Path=/;HttpOnly;");

        // without max age, cookie will stay in memory not in disk
        cookie.setMaxAge(60 * 60 * 24 * 365);
        log.info("write cookie name: {}, cookie value: {} ", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            log.info("read cookie name: {}, cookie value: {}", cookie.getName(), cookie.getValue());
            if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void deleteLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                cookie.setDomain(COOKIE_DOMAIN);
                cookie.setPath("/");
                cookie.setMaxAge(0); // set max age to 0 to delete the cookie
                log.info("delete cookie name: {}, cookie value: {}", cookie.getName(), cookie.getValue());
                response.addCookie(cookie);
                return;
            }
        }
    }

}
