package com.enjoyshop.common.service;

//定义回调函数
public interface Function<T,E> {

	public T callback(E e);
}
