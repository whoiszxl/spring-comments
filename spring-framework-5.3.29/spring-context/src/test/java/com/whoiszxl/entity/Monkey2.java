package com.whoiszxl.entity;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;

public class Monkey2 implements BeanNameAware {

	private String beanName;

	@Override
	public void setBeanName(@NotNull String beanName) {
		System.out.println("BeanNameAware注入的beanName:" + beanName);
	}

	public String getBeanName() {
		return beanName;
	}
}
