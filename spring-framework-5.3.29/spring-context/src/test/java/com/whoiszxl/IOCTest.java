package com.whoiszxl;

import com.whoiszxl.entity.Monkey;
import com.whoiszxl.entity.Monkey2;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class IOCTest {

	private static final String XML_FILE = "whoiszxl.xml";

	@Test
	public void testXmlBeanFactory() {
		XmlBeanFactory xmlBeanFactory = new XmlBeanFactory(
				new ClassPathResource(XML_FILE));
		Monkey monkey = (Monkey)xmlBeanFactory.getBean("monkey");
		System.out.println("xmlBeanFactory测试案例输出:" + monkey.getName());
	}


	@Test
	public void testXmlBeanFactory2() {
		XmlBeanFactory xmlBeanFactory = new XmlBeanFactory(
				new ClassPathResource(XML_FILE));
		Monkey2 monkey = (Monkey2)xmlBeanFactory.getBean("monkey2");
		System.out.println("xmlBeanFactory测试案例输出:" + monkey.getBeanName());
	}

	@Test
	public void testIOC() {
		ApplicationContext applicationContext
				= new ClassPathXmlApplicationContext(XML_FILE);
		Monkey monkey = applicationContext.getBean("monkey", Monkey.class);
		System.out.println(monkey.getName());
	}


}
