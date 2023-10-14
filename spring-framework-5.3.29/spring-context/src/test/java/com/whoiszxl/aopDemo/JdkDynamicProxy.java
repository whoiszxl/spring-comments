package com.whoiszxl.aopDemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicProxy implements InvocationHandler {

	private Object target;

	public JdkDynamicProxy(Object target) {
		this.target = target;
	}

	public Object getProxy() {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("JDK动态代理 start");
		Object invoke = method.invoke(target, args);
		System.out.println("JDK动态代理 end");
		return invoke;
	}
}
