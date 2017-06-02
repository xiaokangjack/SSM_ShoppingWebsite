package com.enjoyshop.web.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.enjoyshop.common.service.ApiService;
import com.enjoyshop.web.bean.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CartService {

    @Autowired
    private ApiService apiService;

    @Value("${ENJOYSHOP_CART_URL}")
    private String ENJOYSHOP_CART_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<Cart> queryCartListByUserId(Long userId) {
        // 查询购物车系统提供的接口获取购物车列表
        try {
            String url = ENJOYSHOP_CART_URL + "/service/cart?userId=" + userId;
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }
            return MAPPER.readValue(jsonData,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
