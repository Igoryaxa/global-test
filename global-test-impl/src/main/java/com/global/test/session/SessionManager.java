package com.global.test.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.global.test.model.Role;
import com.global.test.model.User;

@Component
public class SessionManager {
	// hard code config value for the sake of simplicity
	private static final long SESSION_TIMEOUT_MILLIS = 15 * 60 * 1000; //15 minutes
	
	private Map<String, UserSession> sessions = new HashMap<>();

	private InheritableThreadLocal<UserSession> currentSession = new InheritableThreadLocal<>();

	@PostConstruct
	private void runInvalidationThread() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			sessions.values().removeIf(u -> System.currentTimeMillis() >= u.getLastTimeUsed() + SESSION_TIMEOUT_MILLIS);
		} , SESSION_TIMEOUT_MILLIS, SESSION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
	}

	public void loadSessionById(String sessionId) {
		UserSession session = sessions.get(sessionId);
		if (session != null) {
			session.updateLastTimeUsed();
			currentSession.set(session);
		}
	}

	public boolean isSessionExist() {
		return currentSession.get() != null;
	}

	public Long getCurrentUserId() {
		UserSession userSession = currentSession.get();
		return userSession != null ? userSession.getUserId() : null;
	}

	public Role getCurrentUserRole() {
		UserSession userSession = currentSession.get();
		return userSession != null ? userSession.getUserRole() : null;
	}

	public void loginUser(User user) {
		invalidateOldSessionIfExist();
		String sessionId = UUID.randomUUID().toString();
		UserSession userSession = new UserSession(sessionId, user);
		sessions.put(sessionId, userSession);
		currentSession.set(userSession);
	}

	private void invalidateOldSessionIfExist() {
		// skip implementation for the sake of simplicity
	}

	public void logoutUser() {
		UserSession userSession = currentSession.get();
		if (userSession != null) {
			sessions.remove(userSession.getSessionId());
			currentSession.set(null);
		}
	}
}
