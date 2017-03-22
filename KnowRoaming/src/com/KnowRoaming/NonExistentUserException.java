package com.KnowRoaming;

public class NonExistentUserException extends Exception {
	String userId;
	public NonExistentUserException(String msg, String userId) {
		super(msg);
	}
}
