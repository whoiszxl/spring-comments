package com.whoiszxl.defaultAnnotation;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunTest {


	@Test
	public void testComponent() {
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext("com.whoiszxl.defaultAnnotation");
		Boss boss = context.getBean("boss", Boss.class);
		System.out.println(boss);
	}


}
