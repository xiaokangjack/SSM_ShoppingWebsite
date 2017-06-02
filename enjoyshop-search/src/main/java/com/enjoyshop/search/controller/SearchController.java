package com.enjoyshop.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.enjoyshop.search.bean.SearchResult;
import com.enjoyshop.search.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam("q") String keyWords,
			@RequestParam(value = "page", defaultValue = "1") Integer page) {
		ModelAndView mv = new ModelAndView("search");
		try {
		        //解决中文乱码问题
		        keyWords=new String(keyWords.getBytes("ISO-8859-1"),"UTF-8");
			//搜索结果
		        SearchResult searchResult = this.searchService.search(keyWords, page);
			//搜索关键字
		        mv.addObject("query", keyWords);
			//商品列表
		        mv.addObject("itemList", searchResult.getData());
			//当前页
		        mv.addObject("page", page);
			//计算总页数
		        int total = searchResult.getTotal().intValue();
			int pages = total % SearchService.ROWS == 0 ? total / SearchService.ROWS : total / SearchService.ROWS + 1;
			mv.addObject("pages", pages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
}
