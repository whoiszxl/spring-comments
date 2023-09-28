package com.whoiszxl.entity.cycle;

public class Teacher {

	private Student student;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	@Override
	public String toString() {
		return "Teacher{" +
				"student=" + student +
				'}';
	}
}
