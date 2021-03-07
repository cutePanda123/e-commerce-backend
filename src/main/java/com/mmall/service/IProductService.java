package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    public ServerResponse addOrUpdateProduct(Product product);

    public ServerResponse updateSaleStatus(Integer productId, Integer status);

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
}
