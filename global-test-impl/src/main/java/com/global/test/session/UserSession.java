package com.global.test.session;

import com.global.test.model.Role;
import com.global.test.model.User;

public class UserSession {
	private String sessionId;
	private Long userId;
	private Role userRole;
	private long lastTimeUsed;
	
	public UserSession(String sessionId, User user) {
		this.sessionId = sessionId;
		this.userId = user.getId();
		this.userRole = user.getRole();
		updateLastTimeUsed();
	}
	
	public Long getUserId() {
		return userId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public long getLastTimeUsed() {
		return lastTimeUsed;
	}
	public Role getUserRole() {
		return userRole;
	}
	public void updateLastTimeUsed() {
		this.lastTimeUsed = System.currentTimeMillis(); 
	}
}
