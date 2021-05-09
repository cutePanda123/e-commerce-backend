package com.mmall.controller.admin;

import com.mmall.common.Constants;
import com.mmall.common.RedisShardedPool;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(
            String username,
            String password,
            HttpSession session,
            HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Constants.Role.ROLE_ADMIN) {
                //session.setAttribute(Constants.CURRENT_USER, user);
                CookieUtil.writeLoginToken(httpServletResponse, session.getId());
                RedisShardedPoolUtil.setEx(
                        session.getId(),
                        JsonUtil.obj2str(response.getData()),
                        Constants.RedisCacheExpirationTime.REDIS_SESSION_EXPIRATION_TIME
                );
                return response;
            }
            return ServerResponse.createByErrorMessage("user is not a admin");
        }
        return response;
    }
}
