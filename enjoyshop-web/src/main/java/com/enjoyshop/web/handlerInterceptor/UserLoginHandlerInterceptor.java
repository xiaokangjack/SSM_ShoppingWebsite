package com.enjoyshop.web.handlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.enjoyshop.common.utils.CookieUtils;
import com.enjoyshop.web.bean.User;
import com.enjoyshop.web.service.PropertieService;
import com.enjoyshop.web.service.UserService;
import com.enjoyshop.web.threadlocal.UserThreadLocal;

public class UserLoginHandlerInterceptor implements HandlerInterceptor {

    public static final String COOKIE_NAME = "TT_TOKEN";

    @Autowired
    private PropertieService propertieService;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        UserThreadLocal.set(null);//清空当前线程中的User对象
        
        String loginUrl = propertieService.ENJOYSHOP_SSO_URL + "/user/login.html";
        String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
        if (StringUtils.isEmpty(token)) {
            // 未登录状态
            response.sendRedirect(loginUrl);
            return false;
        }

        User user = this.userService.queryUserByToken(token);
        if (null == user) {
            // 未登录状态
            response.sendRedirect(loginUrl);
            return false;
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
