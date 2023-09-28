package com.whoiszxl.entity.cycle;

public class Student {

	private Teacher teacher;

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	@Override
	public String toString() {
		return "Student{" +
				"teacher=" + teacher +
				'}';
	}
}
