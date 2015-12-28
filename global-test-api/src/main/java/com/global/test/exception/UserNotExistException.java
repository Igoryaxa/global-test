package com.global.test.exception;

public class UserNotExistException extends ApplicationException {
	private String userEmail;

	public UserNotExistException(String userEmail) {
		super(String.format("User with email 1%s does not exist", userEmail));
		this.userEmail = userEmail;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
}
