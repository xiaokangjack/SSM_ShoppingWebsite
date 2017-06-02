package com.enjoyshop.manage.mapper;

import java.util.List;

import com.enjoyshop.manage.pojo.Content;
import com.github.abel533.mapper.Mapper;

public interface ContentMapper extends Mapper<Content>{

	List<Content> queryList(Long categoryId);

}
