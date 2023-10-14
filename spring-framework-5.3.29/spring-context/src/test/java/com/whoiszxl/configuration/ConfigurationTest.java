package com.whoiszxl.configuration;

import com.whoiszxl.configuration.TestBean;
import com.whoiszxl.configuration.config.TestConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConfigurationTest {


	@Test
	public void testComponent() {
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext("com.whoiszxl.configuration.config");
		TestBean testBean = context.getBean("testBean", TestBean.class);
		TestConfig testConfig = context.getBean("testConfig", TestConfig.class);
		System.out.println("testBean:" + testBean);
		System.out.println("testConfig:" + testConfig);
	}


}
