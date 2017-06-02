package com.enjoyshop.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.enjoyshop.common.service.ApiService;
import com.enjoyshop.common.service.RedisService;
import com.enjoyshop.manage.pojo.ItemDesc;
import com.enjoyshop.web.bean.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class ItemService {

	@Autowired
	private ApiService apiService;
	@Value("${ENJOYSHOP_MANAGE_URL}")
	private String ENJOYSHOP_MANAGE_URL;

	private static final ObjectMapper MAPPER = new ObjectMapper();
	public static final String REDIS_KEY="ENJOYSHOP_WEB_ITEM_DETAIL_";
	private static final Integer REDIS_TIME=60*60*24;
	
	@Autowired
	private RedisService redisService;

	//获取商品基本信息
	public Item queryItemById(Long itemId) {
		String key=REDIS_KEY+itemId;
		try {
			//先从缓存中命中
			String cacheData=this.redisService.get(key);
			if(StringUtils.isNotEmpty(cacheData)){
				return MAPPER.readValue(cacheData, Item.class);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			String url = ENJOYSHOP_MANAGE_URL + "/rest/item/" + itemId;
			String jsonData = this.apiService.doGet(url);
			if (StringUtils.isEmpty(jsonData)) {
				return null;
			}
			try {//需要加try catch防止影响原逻辑
				//写入缓存
				this.redisService.set(key, jsonData, REDIS_TIME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return MAPPER.readValue(jsonData, Item.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//获取商品描述信息
	public ItemDesc queryItemDescByItemId(Long itemId) {
		try {
			String url = ENJOYSHOP_MANAGE_URL + "/rest/item/desc/" + itemId;
			String jsonData = this.apiService.doGet(url);
			if (StringUtils.isEmpty(jsonData)) {
				return null;
			}
			return MAPPER.readValue(jsonData, ItemDesc.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//获取商品规格参数信息
	public String queryItemParamByItemId(Long itemId) {
		try {
			String url = ENJOYSHOP_MANAGE_URL + "/rest/item/param/item/" + itemId;
			String jsonData = this.apiService.doGet(url);
			// 解析JSON
			JsonNode jsonNode = MAPPER.readTree(jsonData);
			ArrayNode paramData = (ArrayNode) MAPPER.readTree(jsonNode.get("paramData").asText());
			StringBuilder sb = new StringBuilder();
			sb.append(
					"<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\"><tbody>");
			for (JsonNode param : paramData) {
				sb.append("<tr><th class=\"tdTitle\" colspan=\"2\">" + param.get("group").asText() + "</th></tr>");
				ArrayNode params = (ArrayNode) param.get("params");
				for (JsonNode p : params) {
					sb.append("<tr><td class=\"tdTitle\">" + p.get("k").asText() + "</td><td>" + p.get("v").asText()
							+ "</td></tr>");
				}
			}
			sb.append("</tbody></table>");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
