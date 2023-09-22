package com.whoiszxl.entity;

public class Tiger {

	private String name;

	private Integer age;

	public Tiger(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		return "Tiger{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
