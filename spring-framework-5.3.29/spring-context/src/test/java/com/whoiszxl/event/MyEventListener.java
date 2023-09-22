package com.whoiszxl.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class MyEventListener implements ApplicationListener {
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof MyEvent) {
			((MyEvent)event).sendEvent();
		}
	}
}
