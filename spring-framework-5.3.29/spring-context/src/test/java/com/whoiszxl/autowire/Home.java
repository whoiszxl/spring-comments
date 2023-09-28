package com.whoiszxl.autowire;

import org.springframework.beans.factory.annotation.Autowired;

public class Home {

	@Autowired
	private Person person;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
