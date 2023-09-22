package com.whoiszxl.event;

import org.springframework.context.ApplicationEvent;

public class MyEvent extends ApplicationEvent {
	/**
	 * Create a new {@code ApplicationEvent}.
	 *
	 * @param source the object on which the event initially occurred or with
	 *               which the event is associated (never {@code null})
	 */
	public MyEvent(Object source) {
		super(source);
	}


	public void sendEvent() {
		System.out.println("发送一个自定义事件" + source);
	}
}
