package com.whoiszxl.entity.factory;

import com.whoiszxl.entity.Monkey;
import org.springframework.beans.factory.FactoryBean;

public class MonkeyFactoryBean implements FactoryBean {
	@Override
	public Object getObject() throws Exception {
		Monkey monkey = new Monkey();
		monkey.setName("小孙") ;
		return monkey;
	}

	@Override
	public Class<?> getObjectType() {
		return Monkey.class;
	}
}
