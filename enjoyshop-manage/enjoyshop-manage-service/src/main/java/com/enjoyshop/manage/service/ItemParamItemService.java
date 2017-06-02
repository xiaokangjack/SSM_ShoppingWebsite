package com.enjoyshop.manage.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enjoyshop.manage.mapper.ItemParamItemMapper;
import com.enjoyshop.manage.pojo.ItemParamItem;
import com.github.abel533.entity.Example;

@Service
public class ItemParamItemService extends BaseService<ItemParamItem>{

	@Autowired
	private ItemParamItemMapper itemParamItemMapper;
	public void updateItemParamItem(Long itemId, String itemParams) {
		ItemParamItem record=new ItemParamItem();
		record.setParamData(itemParams);
		record.setUpdated(new Date());
		
		Example example=new Example(ItemParamItem.class);
		example.createCriteria().andEqualTo("itemId", itemId);
		//根据条件做更新，只更新不为null的字段
/*
 updateByExampleSelective(@Param("record") Xxx record, @Param("example") XxxExample example);
第一个参数 是要修改的部分值组成的对象，其中有些属性为null则表示该项不修改。
第二个参数 是一个对应的查询条件的类， 通过这个类可以实现 order by 和一部分的where 条件。
 */

		this.itemParamItemMapper.updateByExampleSelective(record, example);
	}

}
