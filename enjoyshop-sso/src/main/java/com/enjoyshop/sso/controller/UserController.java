package com.enjoyshop.sso.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enjoyshop.common.utils.CookieUtils;
import com.enjoyshop.sso.pojo.User;
import com.enjoyshop.sso.service.UserService;

@RequestMapping("user")
@Controller
public class UserController {
	
	public static final String COOKIE_NAME="TT_TOKEN";

	@Autowired
	private UserService userService;

	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String toRegister() {
		return "register";
	}
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String toLogin() {
		return "login";
	}

	@RequestMapping(value = "check/{param}/{type}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> check(@PathVariable("param") String param, @PathVariable("type") Integer type) {
		try {
			Boolean bool = this.userService.check(param, type);
			if (null == bool) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			// 前端逻辑有问题，这里只有用!bool才能得到正确的结果
			return ResponseEntity.ok(!bool);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

	}

	@RequestMapping(value = "doRegister", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doRegister(@Valid User user,BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(bindingResult.hasErrors()){
			//校验有误
			List<String> msgs=new ArrayList<String>();
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			for (ObjectError objectError : allErrors) {
				String msg = objectError.getDefaultMessage();
				msgs.add(msg);
			}
			result.put("status", "400");
			result.put("data", StringUtils.join(msgs, '|'));
			return result;
		}
		Boolean bool = this.userService.saveUser(user);
		if (bool) {
			// 注册成功
			result.put("status", "200");
		} else {
			result.put("status", "300");
			result.put("data", "注册失败，请重新注册！");
		}
		return result;
	}

	
	
	@RequestMapping(value = "doLogin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doLogin(@RequestParam("username") String username,
			@RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String token = this.userService.doLogin(username, password);
			if (null == token) {
				// 登录失败
				result.put("status", 400);
			} else {
				// 登录成功，需要将token写入到cookie中
				result.put("status", 200);
				CookieUtils.setCookie(request, response, COOKIE_NAME, token);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 登录失败
			result.put("status", 500);
		}
		return result;
	}

	@RequestMapping(value = "{token}", method = RequestMethod.GET)
	public ResponseEntity<User> queryUserByToken(@PathVariable("token") String token) {
		try {
			User user = this.userService.queryUserByToken(token);
			if (null == user) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
