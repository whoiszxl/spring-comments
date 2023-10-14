package com.whoiszxl.configuration.config;

import org.springframework.stereotype.Component;

@Component
public class TestBean2 {

	private String name = "whoiszxl";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TestBean{" +
				"name='" + name + '\'' +
				'}';
	}
}
