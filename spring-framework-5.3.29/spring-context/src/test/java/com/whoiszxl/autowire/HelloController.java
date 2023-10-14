package com.whoiszxl.autowire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class HelloController {

	@Autowired(required = true)
	private HelloService helloService;

	@Autowired(required = false)
	private Home home;

	private Person person;

	@Autowired
	public void setPerson(Person person) {
		this.person = person;
	}

	public String sayHello(String name) {
		System.out.println(helloService.sayHello(name));
		return "ok";
	}

	@PostConstruct
	public void myInit1() {
		System.out.println("HelloController init...");
	}

	@PostConstruct
	public void myInit2() {
		System.out.println("HelloController init...");
	}


	@PreDestroy
	public void myDestroy1() {
		System.out.println("HelloController destroy...");
	}

	@PreDestroy
	public void myDestroy2() {
		System.out.println("HelloController destroy...");
	}
}
