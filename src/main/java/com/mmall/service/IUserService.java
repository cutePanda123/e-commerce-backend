package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    public ServerResponse<User> login(String username, String password);

    public ServerResponse<String> register(User user);

    public ServerResponse<String> isUnregisteredUserIdentity(String userIdentity, String userIdentityType);

    public ServerResponse getSecurityQuestion(String username);

    public ServerResponse checkSecurityQuestionAnswer(String username, String question, String answer);
}
