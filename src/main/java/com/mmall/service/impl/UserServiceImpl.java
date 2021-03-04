package com.mmall.service.impl;

import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("user does not exist");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password)

        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("invalid password");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("login success", user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse usernameChecking = isUnregisteredUserIdentity(user.getUsername(), Constants.USERNAME);
        if (!usernameChecking.isSuccess()) {
            return usernameChecking;
        }
        ServerResponse emailChecking = isUnregisteredUserIdentity(user.getEmail(), Constants.EMAIL);
        if (!emailChecking.isSuccess()) {
            return emailChecking;
        }

        user.setRole(Constants.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int insertionCount = userMapper.insert(user);
        if (insertionCount == 0) {
            return ServerResponse.createByErrorMessage("register failed");
        }
        return ServerResponse.createBySuccessMessage("register success");
    }

    public ServerResponse<String> isUnregisteredUserIdentity(String userIdentity, String userIdentityType) {
        if (org.apache.commons.lang3.StringUtils.isBlank(userIdentityType)) {
            return ServerResponse.createByErrorMessage("invalid params");
        }
        if (Constants.USERNAME.equals(userIdentityType)) {
            if (userMapper.checkUsername(userIdentity) > 0) {
                return ServerResponse.createByErrorMessage("username exists");
            }
        }
        if (Constants.EMAIL.equals(userIdentityType)) {
            if (userMapper.checkEmail(userIdentity) > 0) {
                return ServerResponse.createByErrorMessage("email exists");
            }
        }
        return ServerResponse.createBySuccessMessage("available user identity");
    }

    public ServerResponse getSecurityQuestion(String username) {
        ServerResponse response = isUnregisteredUserIdentity(username, Constants.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("user does not exist");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (org.apache.commons.lang3.StringUtils.isBlank(question)) {
            return ServerResponse.createByErrorMessage("invalid security question");
        }
        return ServerResponse.createBySuccess(question);
    }

    public ServerResponse checkSecurityQuestionAnswer(String username, String question, String answer) {
        int resoutCount = userMapper.checkAnswer(username, question, answer);
        if (resoutCount == 0) {
            return ServerResponse.createByErrorMessage("answer is wrong");
        }
        String token = UUID.randomUUID().toString();
        TokenCache.setKey(Constants.CACHE_TOKEN_PREFIX + username, token);
        return ServerResponse.createBySuccess(token);
    }

    public ServerResponse<String> resetPasswordWithToken(String username, String newPassword, String token) {
        if (org.apache.commons.lang3.StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token is blank");
        }
        ServerResponse response = isUnregisteredUserIdentity(username, Constants.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("user does not exist");
        }
        String savedToken = TokenCache.getKey(Constants.CACHE_TOKEN_PREFIX + username);
        if (org.apache.commons.lang3.StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("input token is blank");
        }
        if (!org.apache.commons.lang3.StringUtils.equals(savedToken, token)) {
            return ServerResponse.createByErrorMessage("input token is wrong");
        }
        String encryptedPassword = MD5Util.MD5EncodeUtf8(newPassword);
        int rowCount = userMapper.updatePasswordByUsername(username, newPassword);
        if (rowCount == 0) {
            return ServerResponse.createByErrorMessage("reset password failed");
        }
        return ServerResponse.createBySuccessMessage("reset password success");
    }

    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        int resultCount = userMapper.checkPasswordByUserId(MD5Util.MD5EncodeUtf8(oldPassword), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("wrong old password");
        }
        user.setPassword((MD5Util.MD5EncodeUtf8(newPassword)));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount == 0) {
            return ServerResponse.createByErrorMessage("reset password failed");
        }
        return ServerResponse.createBySuccessMessage("reset password success");
    }

    public ServerResponse<User> updateUserInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("cannot use existing email");
        }
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPhone(user.getPhone());
        updatedUser.setSecurityQuestion(user.getSecurityQuestion());
        updatedUser.setSecurityAnswer(user.getSecurityAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updatedUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("user information update success", updatedUser);
        }
        return ServerResponse.createByErrorMessage("user information update failed");
    }

    public ServerResponse<User> getUserInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return  ServerResponse.createByErrorMessage("cannot find this user");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
