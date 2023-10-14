package com.whoiszxl.bridgeMethod;

public class Child extends Parent<String> {

	@Override
	public Integer echo() {
		System.out.println("child echo");
		return 2;
	}

	@Override
	public void say(String data) {
		System.out.println("child " + data);
	}
}
