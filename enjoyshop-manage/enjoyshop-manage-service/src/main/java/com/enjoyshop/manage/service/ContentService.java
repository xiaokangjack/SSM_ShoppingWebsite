package com.enjoyshop.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enjoyshop.common.bean.EasyUIResult;
import com.enjoyshop.manage.mapper.ContentMapper;
import com.enjoyshop.manage.pojo.Content;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ContentService extends BaseService<Content>{

	@Autowired
	private ContentMapper contentMapper;
	//查询内容列表
	public EasyUIResult queryList(Long categoryId, Integer page, Integer rows) {
		PageHelper.startPage(page, rows);//设置分页参数
		List<Content> contents=this.contentMapper.queryList(categoryId);
		PageInfo<Content> pageInfo=new PageInfo<Content>(contents);
		return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
	}


}
