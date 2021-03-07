package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    public ServerResponse addOrUpdateProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("add or update product wrong argument");
        }
        if (StringUtils.isNoneBlank(product.getSubImages())) {
            String[] images = product.getSubImages().split(",", 0);
            if (images.length > 0) {
                product.setMainImageUrl(images[0]);
            }
        }
        if (product.getId() != null) {
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("update product success");
            }
            return ServerResponse.createByErrorMessage("update product failed");
        } else {
            int rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("add product success");
            }
            return ServerResponse.createByErrorMessage("add product failed");
        }
    }

    public ServerResponse updateSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setStatus(status);
        product.setId(productId);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("update product sale status success");
        }
        return ServerResponse.createByErrorMessage("update product sale status failed");
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "invalid product id");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("product does not exist");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo<> assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImageUrl(product.getMainImageUrl());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        // imageHost: get from config file or config server
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.mmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        productDetailVo.setParentCategoryId(category == null ? 0 : category.getParentId());  // default is root category

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> listProducts(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListItemVo> productListItemVos = Lists.newArrayList();
        for (Product product : productList) {
            ProductListItemVo productListItemVo = assembleProductListItemVo(product);
            productListItemVos.add(productListItemVo);
        }
        PageInfo pageInfo = new PageInfo(productListItemVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<PageInfo> searchProducts(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNoneBlank(productName)) {
            productName = new StringBuffer().append("%").append(pageName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByProductNameAndProductId(productName, productId);
        List<ProductListItemVo> productListItemVos = Lists.newArrayList();
        for (Product product : productList) {
            ProductListItemVo productListItemVo = assembleProductListItemVo(product);
            productListItemVos.add(productListItemVo);
        }
        PageInfo pageInfo = new PageInfo(productListItemVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListItemVo assembleProductListItemVo(Product product) {
        ProductListItemVo productListItemVo = new ProductListItemVo();
        productListItemVo.setId(product.getId());
        productListItemVo.setSubtitle(product.getSubtitle());
        productListItemVo.setPrice(product.getPrice());
        productListItemVo.setMainImageUrl(product.getMainImageUrl());
        productListItemVo.setCategoryId(product.getCategoryId());
        productListItemVo.setStatus(product.getStatus());

        // imageHost: get from config file or config server
        productListItemVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.mmall.com/"));
        return productListItemVo;
    }
}
