package com.whoiszxl.aopDemo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class TestAspect {


	@Pointcut("execution(public * com.whoiszxl.aopDemo.LoginServiceImpl.login(..))")
	public void pointcut() {}


	@Before(value = "pointcut()")
	public void beforeTest(JoinPoint joinPoint) {
		System.out.printf("TEST开始执行, 方法：%s, 参数：%s \n", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
	}

	@AfterReturning(value = "pointcut()", returning = "returnValue")
	public void afterTest(JoinPoint joinPoint, Object returnValue) {
		System.out.printf("TEST结束执行, 方法：%s, 参数：%s \n", joinPoint.getSignature().getName(), returnValue);
	}
}
