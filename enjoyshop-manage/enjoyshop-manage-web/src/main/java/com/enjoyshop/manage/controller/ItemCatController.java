package com.enjoyshop.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enjoyshop.manage.pojo.ItemCat;
import com.enjoyshop.manage.service.ItemCatService;

@RequestMapping("item/cat")
@Controller
public class ItemCatController {

	@Autowired
	private ItemCatService itemCatService;
    //defaultValue = "0"是第一加载从根节点查询
	//之后的查询根据/rest/item/cat?id中的id值查找相应的数据
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ItemCat>> queryItemCatListByParentId(
			@RequestParam(value = "id", defaultValue = "0") Long parentId) {
		try {
			ItemCat record = new ItemCat();
			record.setParentId(parentId);
			// List<ItemCat> list
			// =this.itemCatService.queryItemCatListByParentId(parentId);
			List<ItemCat> list = this.itemCatService.queryListByWhere(record);
			if (null == list || list.isEmpty()) {
				// 资源不存在
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	
}
