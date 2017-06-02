package com.enjoyshop.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 * 通用页面跳转控制，通过@PathVariable获取占位符{pageName}
 * url形式为/page/pageName。通过获取pagename的值来跳转到不同的页面
 */
@RequestMapping("page")
@Controller
public class PageController {
	@RequestMapping(value="{pageName}",method=RequestMethod.GET)
	public String toPage(@PathVariable("pageName") String pageName){
		return pageName;
	}

}
