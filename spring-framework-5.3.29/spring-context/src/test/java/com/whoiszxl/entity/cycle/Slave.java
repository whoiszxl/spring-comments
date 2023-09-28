package com.whoiszxl.entity.cycle;

public class Slave {

	private Master master;

	public Slave(Master master) {
		this.master = master;
	}

	public Master getMaster() {
		return master;
	}

	public void setMaster(Master master) {
		this.master = master;
	}

	@Override
	public String toString() {
		return "Slave{" +
				"master=" + master +
				'}';
	}
}
