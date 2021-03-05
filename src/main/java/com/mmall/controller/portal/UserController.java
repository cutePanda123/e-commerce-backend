package com.mmall.controller.portal;

import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Constants.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "healthcheck.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> healthcheck() {
        return ServerResponse.createBySuccess("service is healthy");
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Constants.CURRENT_USER);
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
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
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

    @RequestMapping(value = "reset_froget_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetForgetPassword(String username, String newPassword, String token) {
        return iUserService.resetPasswordWithToken(username, newPassword, token);
    }

    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String oldPassword, String newPassword) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("user has not login");
        }
        return iUserService.resetPassword(oldPassword, newPassword, user);
    }

    @RequestMapping(value = "update_user_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        User savedUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (savedUser == null) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        user.setId((savedUser.getId()));
        user.setUsername(savedUser.getUsername());
        ServerResponse<User> response = iUserService.updateUserInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(savedUser.getUsername());
            session.setAttribute(Constants.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_user_information.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.NEED_LOGIN.getCode(),
                    "user did not login, force user to login with response code 10");
        }
        return iUserService.getUserInformation(currentUser.getId());
    }
}
