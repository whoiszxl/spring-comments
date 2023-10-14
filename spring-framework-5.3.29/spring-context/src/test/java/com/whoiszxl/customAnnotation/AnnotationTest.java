package com.whoiszxl.customAnnotation;

import com.whoiszxl.init.InitService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AnnotationTest {

	@Test
	public void testInitMethod() {
		Class<LoginController> loginControllerClass = LoginController.class;
		boolean annotationPresent = loginControllerClass.isAnnotationPresent(MyController.class);
		if(annotationPresent) {
			System.out.println("获得注解：" + loginControllerClass.getAnnotation(MyController.class).value());
			return;
		}
		System.out.println("没有获取到注解");
	}
}
