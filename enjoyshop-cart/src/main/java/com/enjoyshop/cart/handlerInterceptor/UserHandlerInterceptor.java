package com.enjoyshop.cart.handlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.enjoyshop.cart.bean.User;
import com.enjoyshop.cart.service.UserService;
import com.enjoyshop.cart.threadlocal.UserThreadLocal;
import com.enjoyshop.common.utils.CookieUtils;

public class UserHandlerInterceptor implements HandlerInterceptor {

    public static final String COOKIE_NAME = "TT_TOKEN";

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        UserThreadLocal.set(null);//清空当前线程中的User对象
        
        String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
        if (StringUtils.isEmpty(token)) {
            // 未登录状态,放行
            return true;
        }

        User user = this.userService.queryUserByToken(token);
        if (null == user) {
            // 未登录状态,放行
            return true;
        }
        //处于登录状态
        UserThreadLocal.set(user); //将User对象放置到ThreadLocal中
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {

    }

}
