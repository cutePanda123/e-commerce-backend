package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    public ServerResponse addOrUpdateProduct(Product product);

    public ServerResponse updateSaleStatus(Integer productId, Integer status);

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    public ServerResponse<PageInfo> listProducts(int pageNum, int pageSize);

    public ServerResponse<PageInfo> searchProducts(String productName, Integer productId, int pageNum, int pageSize);

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,
                                                                Integer categoryId,
                                                                int pageNum,
                                                                int pageSize,
                                                                String orderBy);
}
