package com.mmall.common.interceptors;

import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        StringBuffer buffer = new StringBuffer();
        Map paraMap = httpServletRequest.getParameterMap();
        Iterator it = paraMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String) entry.getKey();
            String value = StringUtils.EMPTY;
            Object obj = entry.getValue();
            if (obj instanceof String[]) {
                String[] strs = (String[]) obj;
                value = Arrays.toString(strs);
            }
            buffer.append(key).append("=").append(value);
        }

        User user = null;
        String token = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtil.isNotEmpty(token)) {
            String userInfoStr = RedisShardedPoolUtil.get(token);
            user = JsonUtil.str2obj(userInfoStr, User.class);
        }
        log.info(
            "authority interceptor catches incoming request, class name: {}, method name:{}, param: {}",
            className,
            methodName,
            paraMap
        );
        if (user == null || (user.getRole().intValue() != Constants.Role.ROLE_ADMIN)) {
            httpServletResponse.reset(); // without reset, getWriter() has already be set exception will be thrown
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");

            PrintWriter out = httpServletResponse.getWriter();
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImageUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "user does not login");
                    out.print(JsonUtil.obj2str(resultMap));
                } else {
                    out.print(JsonUtil.obj2str(ServerResponse.createByErrorMessage("authority interceptor: no login user")));
                }
            } else {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImageUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "admin only: permission denied");
                    out.print(JsonUtil.obj2str(resultMap));
                } else {
                    out.print(JsonUtil.obj2str(ServerResponse.createByErrorMessage("authority interceptor: no admin user")));
                }
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
