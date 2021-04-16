package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImageUrl;

    private String subImages;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private Byte status;

    private Date createTime;

    private Date updateTime;
}