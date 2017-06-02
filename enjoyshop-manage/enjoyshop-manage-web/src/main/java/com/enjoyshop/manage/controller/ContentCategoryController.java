package com.enjoyshop.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enjoyshop.manage.pojo.ContentCategory;
import com.enjoyshop.manage.service.ContentCategoryService;

@RequestMapping("content/category")
@Controller
public class ContentCategoryController {

	
	@Autowired
	private ContentCategoryService contentCategoryService;
	//分类列表查询，根据父节点id进行查询。第一次查询时不设id，默认值为0
	//url:content/category?id=xx
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ContentCategory>> queryListByParentId(
			@RequestParam(value="id",defaultValue="0") Long parentId){
		
		try {
			ContentCategory record=new ContentCategory();
			record.setParentId(parentId);
			List<ContentCategory> list=this.contentCategoryService.queryListByWhere(record);
			if(null==list||list.isEmpty()){
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		
	}
	//新增内容分类
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<ContentCategory> saveContentCategory(ContentCategory contentCategory){
		
		try {
			this.contentCategoryService.savecontentCategory(contentCategory);
//			contentCategory.setId(null);
//			contentCategory.setIsParent(false);
//			contentCategory.setSortOrder(1);
//			contentCategory.setStatus(1);
//			this.contentCategoryService.save(contentCategory);
//			
//			//判断当前节点的父节点中的isParent属性是否为true
//			ContentCategory parent=this.contentCategoryService.queryById(contentCategory.getParentId());
//			if(!parent.getIsParent()){
//				parent.setIsParent(true);
//				this.contentCategoryService.updateSelective(parent);
//			}
			return ResponseEntity.status(HttpStatus.CREATED).body(contentCategory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	//重命名
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Void> renameContentCategory(ContentCategory contentCategory){
		try {
			this.contentCategoryService.updateSelective(contentCategory);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	
	//删除内容分类
	//这里接受的是delete请求，虽然ajax发起的post请求，但指定了_method=DELETE。SpringMVC会将其转为delete请求来处理
	@RequestMapping(method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(ContentCategory contentCategory){
		try {
			this.contentCategoryService.deleteContentCategory(contentCategory);
//			//递归删除，需要查找待删除节点下的所有子节点
//			List<Object> ids=new ArrayList<Object>();
//			ids.add(contentCategory.getId());
//			findAllSubNode(contentCategory.getId(), ids);
//			//删除所有子节点
//			this.contentCategoryService.deleteByIds(ContentCategory.class, "id", ids);
//			//判断待删除节点的父节点是否有其他子节点，若没有则把父节点的isParent改为false
//			ContentCategory record=new ContentCategory();
//			record.setParentId(contentCategory.getParentId());
//			List<ContentCategory> list=this.contentCategoryService.queryListByWhere(record);
//			if(null==list||list.isEmpty()){
//				ContentCategory parent=new ContentCategory();
//				parent.setId(contentCategory.getParentId());
//				parent.setIsParent(false);
//				this.contentCategoryService.updateSelective(parent);
//			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
//	private void findAllSubNode(Long parentId,List<Object> ids){
//		ContentCategory record=new ContentCategory();
//		record.setParentId(parentId);
//		List<ContentCategory> list=this.contentCategoryService.queryListByWhere(record);
//		for (ContentCategory contentCategory : list) {
//			ids.add(contentCategory.getId());
//			//判断是否为父节点，若是，则递归
//			if(contentCategory.getIsParent()){
//				findAllSubNode(contentCategory.getId(),ids);
//			}
//		}
//	}
	
}
