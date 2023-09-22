package com.whoiszxl.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CustomApplicationContext extends ClassPathXmlApplicationContext {

	public CustomApplicationContext(String location) {
		super(location);
	}

	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition("monkey");
		System.out.println("beanDefinition before:" + beanDefinition.getDescription());
		beanDefinition.setDescription("test");
		System.out.println("beanDefinition after:" + beanDefinition.getDescription());
	}
}
