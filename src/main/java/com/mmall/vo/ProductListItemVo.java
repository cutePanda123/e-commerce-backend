package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListItemVo {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImageUrl;
    private BigDecimal price;
    private Byte status;
    private String imageHost;
}
