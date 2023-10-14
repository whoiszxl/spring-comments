package com.whoiszxl.bridgeMethod;

import org.junit.Test;

import java.lang.reflect.Method;

public class BridgeTest {

	@Test
	public void test() {
		Child child = new Child();
		Method[] declaredMethods = child.getClass().getDeclaredMethods();

		for (Method declaredMethod : declaredMethods) {
			System.out.println(declaredMethod.getDeclaringClass().getName() + " 的 " + declaredMethod.getName() + " 方法是否是桥接方法:" + declaredMethod.isBridge());
		}
	}

}
