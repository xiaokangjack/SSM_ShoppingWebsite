package com.enjoyshop.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enjoyshop.common.bean.EasyUIResult;
import com.enjoyshop.manage.pojo.Content;
import com.enjoyshop.manage.service.ContentService;

@RequestMapping("content")
@Controller
public class ContentController {
	
	@Autowired
	private ContentService contentService;
	
	//新增内容，传递的参数用Content content来接收，
	//因为传过来的表单参数和content对象的属性对应，springMVC可以自动的匹配对象
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> saveContent(Content content){
		try {
			content.setId(null);
			this.contentService.save(content);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	//查询内容列表（在easyui中以表的形式显示，则返回应为EasyUIResult）
	//categoryId是我们自己加的参数，page和rows是easyui内置的两个参数
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryList(@RequestParam("categoryId") Long categoryId,
			@RequestParam(value="page",defaultValue="1") Integer page,
			@RequestParam(value="rows",defaultValue="10") Integer rows){
		try {
			EasyUIResult easyUIResult=this.contentService.queryList(categoryId,page,rows);
			return ResponseEntity.ok(easyUIResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

}
