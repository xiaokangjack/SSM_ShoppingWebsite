package com.enjoyshop.manage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.enjoyshop.manage.pojo.ContentCategory;

@Service
public class ContentCategoryService extends BaseService<ContentCategory> {

	public void savecontentCategory(ContentCategory contentCategory) {
		contentCategory.setId(null);//id由数据库自动生成
		contentCategory.setIsParent(false);
		contentCategory.setSortOrder(1);
		contentCategory.setStatus(1);
		super.save(contentCategory);
		// 判断当前节点的父节点中的isParent属性是否为true
		ContentCategory parent = super.queryById(contentCategory.getParentId());
		if (!parent.getIsParent()) {//不是父节点则需设为父节点
			parent.setIsParent(true);
			super.updateSelective(parent);//更新数据库
		}
	}

	public void deleteContentCategory(ContentCategory contentCategory) {
		//递归删除，需要查找待删除节点下的所有子节点
		List<Object> ids=new ArrayList<Object>();//待删除的节点集合
		ids.add(contentCategory.getId());//首先加入当前节点的id
		findAllSubNode(contentCategory.getId(), ids);//递归查询将所有子节点加入ids
		//删除所有子节点
		super.deleteByIds(ContentCategory.class, "id", ids);
		//判断待删除节点的父节点是否有其他子节点，若没有则把父节点的isParent改为false
		ContentCategory record=new ContentCategory();
		record.setParentId(contentCategory.getParentId());//设置查询条件为：父节点=contentCategory.getParentId()
		List<ContentCategory> list=super.queryListByWhere(record);
		//这个查询到的是父节点为contentCategory.getParentId()的所有节点，也就是contentCategory的兄弟节点
		if(null==list||list.isEmpty()){//待删除节点没有兄弟节点则更新其父节点
			ContentCategory parent=new ContentCategory();
			parent.setId(contentCategory.getParentId());
			parent.setIsParent(false);//取消父节点的标记
			super.updateSelective(parent);
		}
	}
	//递归查找所有子节点
	private void findAllSubNode(Long parentId,List<Object> ids){
		ContentCategory record=new ContentCategory();
		record.setParentId(parentId);
		List<ContentCategory> list=super.queryListByWhere(record);
		for (ContentCategory contentCategory : list) {
			ids.add(contentCategory.getId());//加入ids
			//判断是否为父节点，若是，则递归
			if(contentCategory.getIsParent()){
				findAllSubNode(contentCategory.getId(),ids);
			}
		}
	}

}
