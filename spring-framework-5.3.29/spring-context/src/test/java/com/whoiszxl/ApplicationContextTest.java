package com.whoiszxl;

import com.whoiszxl.context.CustomApplicationContext;
import com.whoiszxl.entity.Monkey;
import com.whoiszxl.entity.Monkey2;
import com.whoiszxl.entity.Tiger;
import com.whoiszxl.event.MyEvent;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ApplicationContextTest {

	private static final String XML_FILE = "whoiszxl.xml";

	@Test
	public void testApplicationContext() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(XML_FILE);
		Monkey monkey = applicationContext.getBean("monkey", Monkey.class);
	 	System.out.println("testApplicationContext测试案例输出:" + monkey);
	}

	@Test
	public void testCustomApplicationContext() {
		ApplicationContext applicationContext = new CustomApplicationContext(XML_FILE);
		Monkey monkey = applicationContext.getBean("monkey-one", Monkey.class);
		System.out.println("testApplicationContext测试案例输出:" + monkey);
	}

	@Test
	public void testPublishEvent() {
		ApplicationContext applicationContext = new CustomApplicationContext(XML_FILE);
		applicationContext.publishEvent(new MyEvent("hello world"));
	}

}
