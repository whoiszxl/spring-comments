package com.whoiszxl.autowire;

import org.springframework.stereotype.Component;

@Component
public class HelloService {

	public String sayHello(String name) {
		return "hello, " + name;
	}
}
