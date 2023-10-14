package com.whoiszxl.configuration.config;

import com.whoiszxl.configuration.TestBean;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 此 Configuration 文件在注解解析的方式里，就和 XML 解析里的xml文件是类似的
 */
@Configuration
public class TestConfig {

	@Scope(SCOPE_PROTOTYPE)
	@Bean(initMethod = "hello", name = {"testBean", "testBean1", "testBean2"}, autowire = Autowire.BY_NAME)
	public TestBean testBean() {
		return new TestBean();
	}

}
