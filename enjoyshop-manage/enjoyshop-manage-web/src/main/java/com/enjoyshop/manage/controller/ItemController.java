package com.enjoyshop.manage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enjoyshop.common.bean.EasyUIResult;
import com.enjoyshop.manage.pojo.Item;
import com.enjoyshop.manage.service.ItemService;
import com.github.pagehelper.PageInfo;

//新增商品时的处理逻辑，前台提交数据的url为/rest/item
@RequestMapping("item")
@Controller
public class ItemController {

	//日志
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemService itemService;

	//新增数据表单，采用post请求，且方法不需要返回实际值
	//传递两个参数desc（商品描述）和itemParams（商品规格）
	//@RequestParam可以直接拿到前端表单input对应的参数，desc和itemParams要和前端中的input标签中的name属性保持一致
	/*前端的对应代码如下：
	 <tr>
	            <td>商品描述:</td>
	            <td>
	                <textarea style="width:800px;height:300px;visibility:hidden;" name="desc"></textarea>
	            </td>
	        </tr>
	        <tr class="params hide">
	        	<td>商品规格:</td>
	        	<td>
	        		
	        	</td>
	        </tr>
	    </table>
	    <input type="hidden" name="itemParams"/>
	
	*/
/*
 springMVC的参数绑定是支持的，例如存在一个类Person,有两个属性name,age,
 json数据为{“name”:zhangsan,"age":10},controller里面方法定义为
 testFun(Person person),即可自动绑定。这里的第一个参数item就使用了自动绑定。
 参考博客：http://www.cnblogs.com/HD/p/4107674.html
 */
/*
/* 
 * 当地址栏为/springmvc/hello.htm?id=10的时候,中有三种接收方式 来获取id
 * 1、String hello(@RequestParam(value = "userid") int id),这样会把地址栏参数名为userid的值赋给参数id,如果用地址栏上的参数名为id,则接收不到 
 * 2、String hello(@RequestParam int id),这种情况下默认会把id作为参数名来进行接收赋值 
 * 3、String hello(int id),这种情况下也会默认把id作为参数名来进行接收赋值 
 * 注:如果参数前面加上@RequestParam注解,如果地址栏上面没有加上该注解的参数,例如:id,那么会报404错误,找不到该路径 
 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> saveItem(Item item, @RequestParam("desc") String desc,
			@RequestParam("itemParams") String itemParams) {
		try {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("新增商品,item={},desc={}", item, desc);
			}
			if (StringUtils.isEmpty(item.getTitle())) {//400
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			this.itemService.saveItem(item, desc, itemParams);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("新增商品成功,itemId={}", item.getId());
			}
			return ResponseEntity.status(HttpStatus.CREATED).build();//201
		} catch (Exception e) {
			LOGGER.error("新增商品失败！title=" + item.getTitle() + ",cid=" + item.getCid(), e);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();//500
	}

	//传入两个参数page和rows，这是分页插件所需的参数
	//url:rest/item?page&rows
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<EasyUIResult> queryItemList(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "rows", defaultValue = "30") Integer rows) {
		try {
			PageInfo<Item> pageInfo = this.itemService.queryPageList(page, rows);
			EasyUIResult easyUIResult = new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
			return ResponseEntity.ok(easyUIResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 出错 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	/**
	 * 修改商品信息
	 * 
	 * @param item
	 * @param desc
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Void> updateItem(Item item, @RequestParam("desc") String desc,
			@RequestParam("itemParams") String itemParams) {
		try {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("修改商品， item = {}, desc = {}", item, desc);
			}
			if (StringUtils.isEmpty(item.getTitle())) {
				// 响应400
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}

			this.itemService.updateItem(item, desc, itemParams);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("修改商品成功， itemId = {}", item.getId());
			}

			// 成功 204
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			LOGGER.error("修改商品失败! title = " + item.getTitle() + ", cid = " + item.getCid(), e);
		}
		// 出错 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 * 根据商品id查询商品数据
	 * 
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value = "{itemId}", method = RequestMethod.GET)
	public ResponseEntity<Item> queryById(@PathVariable("itemId") Long itemId) {
		try {
			Item item = this.itemService.queryById(itemId);
			if (null == item) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 出错 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
}
