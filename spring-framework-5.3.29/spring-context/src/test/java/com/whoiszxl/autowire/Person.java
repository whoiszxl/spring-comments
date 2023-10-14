package com.whoiszxl.autowire;

import org.springframework.stereotype.Component;

@Component
public class Person {

	private String name = "whoiszxl";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
