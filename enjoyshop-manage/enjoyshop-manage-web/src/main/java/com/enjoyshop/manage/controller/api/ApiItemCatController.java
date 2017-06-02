package com.enjoyshop.manage.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.enjoyshop.common.bean.ItemCatResult;
import com.enjoyshop.manage.service.ItemCatService;

//为前端系统提供一个查询的接口
//url:api/item/cat
@RequestMapping("api/item/cat")
@Controller
public class ApiItemCatController {
	
    @Autowired
	private ItemCatService itemCatService;//做查询
    
    //private final static ObjectMapper MAPPER=new ObjectMapper();
    //查询所有商品类目，返回值为一个对象
    //在enjoyshop-manage-servlet.xml中配置了一个自定义的消息转化器，支持了jsonp
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<ItemCatResult> queryItemCat(){
		try {
			ItemCatResult itemCatResult=this.itemCatService.queryAllToTree();
			return ResponseEntity.ok(itemCatResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	//返回一个字符串，没有设置自定义的消息转化器时采用如下方式支持jsonp
//	@RequestMapping(method=RequestMethod.GET)
//	public ResponseEntity<String> queryItemCat(@RequestParam("callback") String callback){
//		try {
//			ItemCatResult itemCatResult=this.itemCatService.queryAllToTree();
//			String json=MAPPER.writeValueAsString(itemCatResult);
//			if(StringUtils.isEmpty(callback)){
//				return ResponseEntity.ok(json);
//			}
//			return ResponseEntity.ok(callback+"("+json+");");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//	}
}
