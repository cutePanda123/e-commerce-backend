package com.mmall.controller.admin;

import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.rmi.server.ServerCloneException;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    IUserService iUserService;

    @Autowired
    IProductService iProductService;

    @Autowired
    IFileService iFileService;

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addProduct(HttpServletRequest request, Product product) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.addOrUpdateProduct(product);
    }

    @RequestMapping(value = "update_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse updateSaleStatus(HttpServletRequest request, Integer productId, Integer status) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.updateSaleStatus(productId, status);
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse manageProductDetail(HttpServletRequest request, Integer productId) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.getProductDetail(productId, true);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse listProducts(
            HttpServletRequest request,
            @RequestParam(value="pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.listProducts(pageNum, pageSize);
    }

    @RequestMapping(value = "search.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse searchProducts(
            HttpServletRequest request,
            Integer productId,
            String productName,
            @RequestParam(value="pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }
        return iProductService.searchProducts(productName, productId, pageNum, pageSize);
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(
            HttpSession session,
            @RequestParam(value = "uploadFile", required = false) MultipartFile file,
            HttpServletRequest request) {
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("user did not login");
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "user does not login");
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            return ServerResponse.createByErrorMessage("admin only: permission denied.");
        }

        String path = request.getSession().getServletContext().getRealPath(Constants.IMAGE_UPLOAD_DIR);
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);
        return ServerResponse.createBySuccess(fileMap);
    }

    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImageUpload(
            HttpServletRequest request,
            @RequestParam(value = "uploadFile", required = false) MultipartFile file,
            HttpServletResponse response
    ) {
        Map resultMap = Maps.newHashMap();
        String token = CookieUtil.readLoginToken(request);
        if (StringUtil.isEmpty(token)) {
            resultMap.put("success", false);
            resultMap.put("msg", "user does not login");
            return resultMap;
        }
        String userInfoStr = RedisShardedPoolUtil.get(token);
        User user = JsonUtil.str2obj(userInfoStr, User.class);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "user does not login");
            return resultMap;
        }
        if (!iUserService.isAdminRole(user).isSuccess()) {
            resultMap.put("success", false);
            resultMap.put("msg", "admin only: permission denied");
            return resultMap;
        }
        // return value will follow simditor's one
        String path = request.getSession().getServletContext().getRealPath(Constants.IMAGE_UPLOAD_DIR);
        String targetFileName = iFileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            resultMap.put("success", false);
            resultMap.put("msg", "ftp file upload failed");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        resultMap.put("success", true);
        resultMap.put("msg", "upload success");
        resultMap.put("file_path", url);

        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }

}
