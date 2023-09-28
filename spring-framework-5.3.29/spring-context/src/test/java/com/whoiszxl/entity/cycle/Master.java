package com.whoiszxl.entity.cycle;

public class Master {

	private Slave slave;

	public Master(Slave slave) {
		this.slave = slave;
	}

	public Slave getSlave() {
		return slave;
	}

	public void setSlave(Slave slave) {
		this.slave = slave;
	}

	@Override
	public String toString() {
		return "Master{" +
				"slave=" + slave +
				'}';
	}
}
