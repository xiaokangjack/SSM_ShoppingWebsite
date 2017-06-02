package com.enjoyshop.manage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enjoyshop.common.bean.EasyUIResult;
import com.enjoyshop.manage.pojo.ItemParam;
import com.enjoyshop.manage.service.ItemParamService;
import com.github.pagehelper.PageInfo;

@RequestMapping("item/param")
@Controller
public class ItemParamController {

	@Autowired
	private ItemParamService itemParamService;
    //对应的url是/rest/item/param/itemCatId
	//根据类目id查询模板数据
	@RequestMapping(value="{itemCatId}",method=RequestMethod.GET)
	public ResponseEntity<ItemParam> queryByItemCatId(@PathVariable("itemCatId") Long itemCatId){
		try {
			ItemParam record=new ItemParam();
			record.setItemCatId(itemCatId);
			ItemParam itemParam=this.itemParamService.queryOne(record);
			if(null==itemParam){
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(itemParam);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	//对应的url是/rest/item/param/itemCatId，和上面的url相同，但是请求方式不同。一个是get，一个是post，这就是rest的功能
	//保存商品规格参数模板
	@RequestMapping(value="{itemCatId}",method=RequestMethod.POST)
	public ResponseEntity<Void> saveItemParam(@RequestParam("paramData") String paramData,@PathVariable("itemCatId") Long itemCatId){
		try {
			ItemParam itemParam=new ItemParam();
			itemParam.setItemCatId(itemCatId);
			itemParam.setParamData(paramData);
			this.itemParamService.save(itemParam);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryItemParamList(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "rows", defaultValue = "30") Integer rows) {
		try {
			PageInfo<ItemParam> pageInfo = this.itemParamService.queryPageList(page, rows);
			EasyUIResult easyUIResult = new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
			return ResponseEntity.ok(easyUIResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 出错 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
