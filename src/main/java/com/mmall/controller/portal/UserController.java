package com.mmall.controller.portal;

import com.mmall.common.Constants;
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
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Constants.CURRENT_USER, response.getData());
        }
        return response;
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
        return iUserService.register(user)
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
}
