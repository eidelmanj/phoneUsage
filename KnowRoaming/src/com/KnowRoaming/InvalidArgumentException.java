package com.KnowRoaming;

public class InvalidArgumentException extends Exception {
	private String typeStr;
	
	public InvalidArgumentException(String typeStr, String errMsg) {
		super(errMsg);
		this.typeStr = typeStr;
	}
	
	
	public String getType() {
		return this.typeStr;
	}
}
