package com.simple.admin.constant;

public class Result {

	public static final int SUCCESS = 1;
	public static final int FAILED = 2;
	
	public Result(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}
	public Result(int status, String msg,Object o) {
		this.status = status;
		this.msg = msg;
		this.o = o;
	}
	private int status;
	private String msg;
	private Object o;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getO() {
		return o;
	}
	public void setO(Object o) {
		this.o = o;
	}
}
