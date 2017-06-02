package com.enjoyshop.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.enjoyshop.web.service.IndexService;

@Controller
public class IndexController {

	@Autowired
	private IndexService indexService;

	@RequestMapping(value = "index", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView index() {
		ModelAndView mv = new ModelAndView("index");
		// 获取大广告位数据
		String indexAD1 = this.indexService.queryIndexAD1();
		//将数据放入request请求中
		mv.addObject("indexAD1", indexAD1);
		// 右上角广告位数据
		String indexAD2 = this.indexService.queryIndexAD2();
		mv.addObject("indexAD2", indexAD2);
		return mv;
	}
}
