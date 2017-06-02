package com.enjoyshop.manage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enjoyshop.common.bean.ItemCatData;
import com.enjoyshop.common.bean.ItemCatResult;
import com.enjoyshop.common.service.RedisService;
import com.enjoyshop.manage.pojo.ItemCat;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ItemCatService extends BaseService<ItemCat> {
	// @Autowired
	// private ItemCatMapper itemCatMapper;
	//
	// public List<ItemCat> queryItemCatListByParentId(Long parentId){
	// ItemCat record=new ItemCat();
	// record.setParentId(parentId);
	// return this.itemCatMapper.select(record);
	// }
	//

	@Autowired
	private RedisService redisService;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String REDIS_KEY = "ENJOYSHOP_MANAGE_ITEM_CAT_ALL";// 最佳实践:项目名_模块名_业务名
	private static final Integer REDIS_TIME = 60 * 60 * 24 * 30 * 3;

	/**
	 * 全部查询，并且生成树状结构
	 * 
	 * @return
	 */
	public ItemCatResult queryAllToTree() {
		ItemCatResult result = new ItemCatResult();//定义结果的序列化json

		try {
			// 先从缓存中命中，如果命中则返回。若未命中则继续执行
            //缓存不能影响原来业务逻辑的执行，所以要放在try-catch语句中
			String cacheData = this.redisService.get(REDIS_KEY);
			if (StringUtils.isNotEmpty(cacheData)) {
				// 命中 返回结果
				return MAPPER.readValue(cacheData, ItemCatResult.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 全部查出，并且在内存中生成树形结构
		List<ItemCat> cats = super.queryAll();

		// 转为map存储，key为父节点ID，value为数据集合
		Map<Long, List<ItemCat>> itemCatMap = new HashMap<Long, List<ItemCat>>();
		for (ItemCat itemCat : cats) {
			//判断当前节点的父节点值是否包含在map的key值中
			//多个子节点共享一个父节点，保证map不会存在重复的key
			if (!itemCatMap.containsKey(itemCat.getParentId())) {
				//key为当前节点的父节点
				//value为一个空的ArrayList集合
				itemCatMap.put(itemCat.getParentId(), new ArrayList<ItemCat>());
			}
			//把当前节点加入到其父节点对应的ArrayList集合中
			//itemCatMap.get(itemCat.getParentId())获取到当前节点的父节点对应的ArrayList集合
			itemCatMap.get(itemCat.getParentId()).add(itemCat);
		}
        //到此，itemCatMap中保存了<父节点id,对应的子节点的集合>
		
		// 封装一级对象
		List<ItemCat> itemCatList1 = itemCatMap.get(0L);//获取根节点id（id=0）对应的子节点集合
		for (ItemCat itemCat : itemCatList1) {
			ItemCatData itemCatData = new ItemCatData();//定义单个节点的序列化json
			itemCatData.setUrl("/products/" + itemCat.getId() + ".html");//设置url
			itemCatData.setName("<a href='" + itemCatData.getUrl() + "'>" + itemCat.getName() + "</a>");//设置name
			result.getItemCats().add(itemCatData);//放入最后结果的序列化json中
			if (!itemCat.getIsParent()) {
				continue;//如果不是父节点就不存在二级对象，直接跳过
			}

			// 封装二级对象，进行到这里说明当前节点一定是父节点
			List<ItemCat> itemCatList2 = itemCatMap.get(itemCat.getId());//获取当前节点的子节点集合
			List<ItemCatData> itemCatData2 = new ArrayList<ItemCatData>();
			itemCatData.setItems(itemCatData2);//初始化一级对象itemCatData中的Items为itemCatData2
			for (ItemCat itemCat2 : itemCatList2) {
				ItemCatData id2 = new ItemCatData();
				id2.setName(itemCat2.getName());
				id2.setUrl("/products/" + itemCat2.getId() + ".html");
				itemCatData2.add(id2);//把二级对象放到itemCatData2中
				if (itemCat2.getIsParent()) {
					// 封装三级对象
					List<ItemCat> itemCatList3 = itemCatMap.get(itemCat2.getId());
					List<String> itemCatData3 = new ArrayList<String>();
					id2.setItems(itemCatData3);
					for (ItemCat itemCat3 : itemCatList3) {
						itemCatData3.add("/products/" + itemCat3.getId() + ".html|" + itemCat3.getName());
					}
				}
			}
			
			//最多显示14个类目，后面不进行显示
			if (result.getItemCats().size() >= 14) {
				break;
			}
		}
		// 将结果集写入缓存
		try {
			this.redisService.set(REDIS_KEY, MAPPER.writeValueAsString(result), REDIS_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
