package com.enjoyshop.web.bean;

import org.apache.commons.lang3.StringUtils;

public class Item extends com.enjoyshop.manage.pojo.Item{

	public String[] getImages(){
		return StringUtils.split(super.getImage(),',');
	}
}
