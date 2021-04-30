package com.mmall.controller.portal;

import com.github.pagehelper.StringUtil;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/user/")
public class UserController {
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
            CookieUtil.writeLoginToken(httpServletResponse, session.getId());
            RedisUtil.setEx(
                    session.getId(),
                    JsonUtil.obj2str(response.getData()),
                    Constants.RedisCacheExpirationTime.REDIS_SESSION_EXPIRATION_TIME);
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.deleteLoginToken(request, response);
        RedisUtil.delete(token);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "is_unregistered.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> isUnregistered(String userIdentity, String identityType) {
        return iUserService.isUnregisteredUserIdentity(userIdentity, identityType);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("user has not login");
    }

    @RequestMapping(value = "get_security_question.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> getSecurityQuestion(String username) {
        return iUserService.getSecurityQuestion(username);
    }

    @RequestMapping(value = "check_security_answer.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkSecurityAnswer(String username, String question, String answer) {
        return iUserService.checkSecurityQuestionAnswer(username, question, answer);
    }

    @RequestMapping(value = "reset_forget_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetForgetPassword(String username, String newPassword, String token) {
        return iUserService.resetPasswordWithToken(username, newPassword, token);
    }

    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest request, String oldPassword, String newPassword) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("user has not login");
        }
        return iUserService.resetPassword(oldPassword, newPassword, user);
    }

    @RequestMapping(value = "update_user_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest request, User user) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User savedUser = JsonUtil.str2obj(userInfoStr, User.class);
        if (savedUser == null) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        user.setId((savedUser.getId()));
        user.setUsername(savedUser.getUsername());
        ServerResponse<User> response = iUserService.updateUserInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(savedUser.getUsername());
            RedisUtil.setEx(
                    token,
                    JsonUtil.obj2str(response.getData()),
                    Constants.RedisCacheExpirationTime.REDIS_SESSION_EXPIRATION_TIME);
        }
        return response;
    }

    @RequestMapping(value = "get_user_information.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisUtil.get(token);
        User currentUser = JsonUtil.str2obj(userInfoStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.NEED_LOGIN.getCode(),
                    "user did not login, force user to login with response code 10");
        }
        return iUserService.getUserInformation(currentUser.getId());
    }
}
