package com.KnowRoaming;

public class NonExistentRecordException extends Exception {
	String userId;
	public NonExistentRecordException(String msg, String userId) {
		super(msg);
	}
}
