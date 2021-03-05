package com.mmall.controller.admin;

import com.mmall.common.ServerResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/manage/healthcheck")
public class HealthcheckController {
    @RequestMapping(value = "/health_check.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> healthcheck() {
        return ServerResponse.createBySuccessMessage("service is healthy.");
    }
}
