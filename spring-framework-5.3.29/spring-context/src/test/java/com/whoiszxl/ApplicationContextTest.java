package com.whoiszxl;

import com.whoiszxl.autowire.Home;
import com.whoiszxl.context.CustomApplicationContext;
import com.whoiszxl.entity.Monkey;
import com.whoiszxl.entity.Monkey2;
import com.whoiszxl.entity.Tiger;
import com.whoiszxl.entity.cycle.Master;
import com.whoiszxl.entity.cycle.Teacher;
import com.whoiszxl.event.MyEvent;
import com.whoiszxl.factory.Phone;
import com.whoiszxl.init.InitService;
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

	@Test
	public void testConstructorCycle() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("cycle.xml");
		Master master = applicationContext.getBean("master", Master.class);
		System.out.println(master);
	}

	@Test
	public void testSetterCycle() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("cycle2.xml");
		Teacher teacher = applicationContext.getBean("teacher", Teacher.class);
		System.out.println(teacher);
	}

	@Test
	public void testFactoryBean() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("factoryBean.xml");
		Monkey monkey = applicationContext.getBean("monkey", Monkey.class);
		System.out.println(monkey);
	}

	@Test
	public void testDependOn() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("dependon.xml");
		Teacher teacher = applicationContext.getBean("teacher", Teacher.class);
		System.out.println(teacher);
	}

	@Test
	public void testFactoryMethod() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("factory.xml");
		Phone phone = applicationContext.getBean("phone", Phone.class);
		System.out.println(phone.getName());
	}

	@Test
	public void testAutowire() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("autowire.xml");
		Home home = applicationContext.getBean("home", Home.class);
		System.out.println(home.getPerson().getName());
	}


	@Test
	public void testInitMethod() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("init.xml");
		InitService initService = applicationContext.getBean("initService", InitService.class);
		System.out.println(initService.getName());
		((ClassPathXmlApplicationContext)applicationContext).close();
	}
}
