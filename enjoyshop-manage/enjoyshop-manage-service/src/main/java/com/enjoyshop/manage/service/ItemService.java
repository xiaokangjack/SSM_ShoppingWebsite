package com.enjoyshop.manage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.enjoyshop.common.service.ApiService;
import com.enjoyshop.manage.mapper.ItemMapper;
import com.enjoyshop.manage.pojo.Item;
import com.enjoyshop.manage.pojo.ItemDesc;
import com.enjoyshop.manage.pojo.ItemParamItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ItemService extends BaseService<Item> {

	@Autowired
	private ItemDescService itemDescService;
	@Autowired
	private ItemParamItemService itemParamItemService;
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ApiService apiService;
	@Value("${ENJOYSHOP_WEB_URL}")
	private String ENJOYSHOP_WEB_URL;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public void saveItem(Item item, String desc, String itemParams) {
		// 默认为上架状态
		item.setStatus(1);
		// 防止前端恶意添加id值
		item.setId(null);
		//保存商品信息
		super.save(item);
		//构建ItemDesc数据并保存
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		this.itemDescService.save(itemDesc);
                //构建ItemParamItem数据并保存
		ItemParamItem itemParamItem = new ItemParamItem();
		itemParamItem.setItemId(item.getId());
		itemParamItem.setParamData(itemParams);
		this.itemParamItemService.save(itemParamItem);
		//发送mq消息
		sendMsg(item.getId(), "insert");
	}

	public PageInfo<Item> queryPageList(Integer page, Integer rows) {
		Example example = new Example(Item.class);
		example.setOrderByClause("updated DESC");
		// 设置分页参数
		PageHelper.startPage(page, rows);
		List<Item> list = this.itemMapper.selectByExample(example);
		return new PageInfo<Item>(list);
	}

	public void updateItem(Item item, String desc, String itemParams) {
		// 强制设置不能修改的字段为null
		item.setStatus(null);
		item.setCreated(null);
		super.updateSelective(item);

		// 修改商品描述数据
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		this.itemDescService.updateSelective(itemDesc);

		// 修改商品规格参数
		this.itemParamItemService.updateItemParamItem(item.getId(), itemParams);

		// try {
		// // 需要通知其他系统进行数据更新
		// String url = ENJOYSHOP_WEB_URL + "/item/cache/" + item.getId() +
		// ".html";
		// this.apiService.doPost(url, null);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		
		//发送mq消息
		sendMsg(item.getId(), "update");
	}
	
	private void sendMsg(long itemId,String type){
		try {
			//发送mq详细通知其他系统进行数据更新
			Map<String, Object> msg = new HashMap<String, Object>();
			msg.put("itemId", itemId);
			msg.put("type", type);
			msg.put("date", System.currentTimeMillis());
			this.rabbitTemplate.convertAndSend("item."+type, MAPPER.writeValueAsString(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
