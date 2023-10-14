package com.whoiszxl.aopDemo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibDynamicProxy implements MethodInterceptor {

	private Object target;

	public CglibDynamicProxy(Object target) {
		this.target = target;
	}

	public Object getProxy() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(this);
		return enhancer.create();
	}

	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("Cglib动态代理 start");
		Object invoke = method.invoke(target, objects);
		System.out.println("Cglib动态代理 end");
		return invoke;
	}
}
