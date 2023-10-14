package com.whoiszxl.bridgeMethod;

public class Parent<T> {

	public Number echo() {
		System.out.println("parent echo");
		return 1;
	}

	public void say(T data) {
		System.out.println("parent " + data);
	}
}
