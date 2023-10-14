package com.whoiszxl.aopDemo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LogAspect {


	@Pointcut("execution(public * com.whoiszxl.aopDemo.LoginServiceImpl.*(..))")
	public void pointcut() {}


	@Before(value = "pointcut()")
	public void before(JoinPoint joinPoint) {
		System.out.printf("before, 方法：%s, 参数：%s \n", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
	}

	@After(value = "pointcut()")
	public void after(JoinPoint joinPoint) {
		System.out.printf("after, 方法：%s, 参数：%s \n", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
	}

	@AfterReturning(value = "pointcut()", returning = "returnValue")
	public void afterReturning(JoinPoint joinPoint, Object returnValue) {
		System.out.printf("afterReturning, 方法：%s, 参数：%s \n", joinPoint.getSignature().getName(), returnValue);
	}

	@AfterThrowing(value = "pointcut()")
	public void afterThrowing(JoinPoint joinPoint) {
		System.out.printf("afterThrowing, 方法：%s \n", joinPoint.getSignature().getName());
	}

	@Around(value = "pointcut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		System.out.println("around前置");
		Object proceed = point.proceed();
		System.out.println("around后置");
		return proceed;
	}

}
