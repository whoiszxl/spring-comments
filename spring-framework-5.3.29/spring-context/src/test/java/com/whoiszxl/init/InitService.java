package com.whoiszxl.init;

import org.springframework.beans.factory.DisposableBean;

public class InitService implements DisposableBean {

	private String name  = "default";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void initName() {
		this.name = "change name";
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("destroy 中 .....");
	}
}
