package com.whoiszxl.defaultAnnotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Boss {

	private String name = "whoiszxl";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Boss{" +
				"name='" + name + '\'' +
				'}';
	}
}
