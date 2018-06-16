package com.lucent.model;

public class SurepayCase {
	private String tid;
	private String fid;
	private String customer;
	
	public SurepayCase(String tid, String fid, String customer) {
		this.tid = tid;
		this.fid = fid;
		this.customer = customer;
	}
	
	public String getTid() {
		return tid;
	}
	
	public void setTid(String tid) {
		this.tid = tid;
	}
	
	public String getFid() {
		return fid;
	}
	
	public void setFid(String fid) {
		this.fid = fid;
	}
	
	public String getCustomer() {
		return customer;
	}
	
	public void setCustomer(String customer) {
		this.customer = customer;
	}

}
