package com.whoiszxl.aopDemo;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AopTest {

	@Test
	public void test1() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("aopDemo.xml");
		LoginService loginServiceImpl = context.getBean("loginServiceImpl", LoginService.class);
		loginServiceImpl.login("whoiszxl", "123456");
		loginServiceImpl.logout("token");
		context.close();
	}


	@Test
	public void test2() {
		JdkDynamicProxy jdkDynamicProxy = new JdkDynamicProxy(new LoginServiceImpl());
		LoginService proxy = (LoginService) jdkDynamicProxy.getProxy();
		proxy.login("whoiszxl", "123456");
	}

	@Test
	public void test3() {
		CglibDynamicProxy cglibDynamicProxy = new CglibDynamicProxy(new LoginServiceImpl());
		LoginService proxy = (LoginService) cglibDynamicProxy.getProxy();
		proxy.login("whoiszxl", "123456");
	}
}
