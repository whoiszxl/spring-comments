package com.whoiszxl.configuration;

public class TestBean {

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

	public void hello() {
		System.out.println("hello");
	}
}
