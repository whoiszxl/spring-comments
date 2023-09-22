package com.whoiszxl.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

public class MyBeanDefinitionProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, Ordered {


	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("postProcessBeanDefinitionRegistry bean count:" + registry.getBeanDefinitionCount());
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("postProcessBeanFactory bean count" + beanFactory.getBeanDefinitionCount());
	}


	@Override
	public int getOrder() {
		return 100;
	}
}
