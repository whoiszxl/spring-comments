package com.whoiszxl.autowire;

import com.whoiszxl.customAnnotation.LoginController;
import com.whoiszxl.customAnnotation.MyController;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AutowireTest {

	@Test
	public void testAutowire() {
		AnnotationConfigApplicationContext context =
				new AnnotationConfigApplicationContext("com.whoiszxl.autowire");
		HelloController helloController = context.getBean("helloController", HelloController.class);
		helloController.sayHello("whoiszxl");
		context.close();
	}
}
