package com.enjoyshop.cart.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enjoyshop.cart.bean.Item;
import com.enjoyshop.cart.pojo.Cart;
import com.enjoyshop.common.utils.CookieUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CartCookieService {

    public static final String COOKIE_NAME = "TT_CART";

    public static final Integer COOKIE_TIME = 60 * 60 * 24 * 30 * 12;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ItemService itemService;

    public void addItemToCart(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = queryCartList(request);

        // 判断该商品在购物车中是否存在
        Cart cart = null;
        for (Cart c : carts) {
            if (c.getItemId().longValue() == itemId.longValue()) {
                cart = c;
                break;
            }
        }

        if (null == cart) {
            // 不存在
            Item item = this.itemService.queryItemById(itemId);
            if (null == item) {
                // TODO 给出用户提示
                return;
            }

            cart = new Cart();
            cart.setCreated(new Date());
            cart.setUpdated(cart.getCreated());
            cart.setItemId(itemId);
            cart.setItemImage(item.getImages()[0]);
            cart.setItemPrice(item.getPrice());
            cart.setItemTitle(item.getTitle());
            cart.setNum(1); // TODO 默认为1

            carts.add(cart);// 加入到购物车
        } else {
            // 该商品存在购物车中，数量相加，默认为1 TODO
            cart.setNum(cart.getNum() + 1);
            cart.setUpdated(new Date());
        }

        // 将集合写入到cookie
        try {
            CookieUtils.setCookie(request, response, COOKIE_NAME, MAPPER.writeValueAsString(carts),
                    COOKIE_TIME, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Cart> queryCartList(HttpServletRequest request) {
        String cookieValue = CookieUtils.getCookieValue(request, COOKIE_NAME, true);
        List<Cart> carts = null;
        if (StringUtils.isEmpty(cookieValue)) {
            carts = new ArrayList<Cart>();
        } else {
            try {
                carts = MAPPER.readValue(cookieValue,
                        MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
            } catch (Exception e) {
                e.printStackTrace();
                carts = new ArrayList<Cart>();
            }
        }
        return carts;
    }

    public void updateNum(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = queryCartList(request);
        // 判断该商品在购物车中是否存在
        for (Cart c : carts) {
            if (c.getItemId().longValue() == itemId.longValue()) {
                c.setNum(num);
                c.setUpdated(new Date());
                break;
            }
        }

        // 将集合写入到cookie
        try {
            CookieUtils.setCookie(request, response, COOKIE_NAME, MAPPER.writeValueAsString(carts),
                    COOKIE_TIME, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = queryCartList(request);
        // 判断该商品在购物车中是否存在
        for (Cart c : carts) {
            if (c.getItemId().longValue() == itemId.longValue()) {
                carts.remove(c);
                break;
            }
        }

        // 将集合写入到cookie
        try {
            CookieUtils.setCookie(request, response, COOKIE_NAME, MAPPER.writeValueAsString(carts),
                    COOKIE_TIME, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
