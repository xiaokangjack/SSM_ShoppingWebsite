package com.enjoyshop.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enjoyshop.manage.mapper.ItemParamMapper;
import com.enjoyshop.manage.pojo.ItemParam;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ItemParamService extends BaseService<ItemParam>{

	@Autowired
	private ItemParamMapper itemParamMapper;
	
	public PageInfo<ItemParam> queryPageList(Integer page, Integer rows) {
		Example example=new Example(ItemParam.class);
		example.setOrderByClause("updated DESC");
		//设置分页参数
		PageHelper.startPage(page,rows);
		List<ItemParam> list=this.itemParamMapper.selectByExample(example);
		return new PageInfo<ItemParam>(list);
	}

}
