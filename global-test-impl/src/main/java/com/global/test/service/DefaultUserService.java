package com.global.test.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.global.test.dao.UserDao;
import com.global.test.exception.SecurityAccessException;
import com.global.test.exception.UserNotExistException;
import com.global.test.model.Role;
import com.global.test.model.User;
import com.global.test.session.SessionManager;

@Service
public class DefaultUserService implements UserService {

	@Autowired
	private SessionManager sessionManager;
	
	@Autowired
	private UserDao userDao;

	@Autowired
    private PasswordEncoder passwordEncoder;
    
	@Override
	public void loginUser(String userEmail, String password) {
		User user = userDao.getUserByEmail(userEmail);
		if (user == null) {
			throw new UserNotExistException(userEmail);
		}
		
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new SecurityAccessException("Password is incorrect");
		}
		
		sessionManager.loginUser(user);
	}

	@Override
	public void logoutUser() {
		sessionManager.logoutUser();
	}

	@Override
	public void resetPassword(String userEmail) {
		User user = userDao.getUserByEmail(userEmail);
		if (user == null) {
			throw new UserNotExistException(userEmail);
		}
		String password = getNewRandomPassword();
		user.setPassword(passwordEncoder.encode(password));
		userDao.updateUser(user);
		sendEmailWithNewPasswordToUser(userEmail, password);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		Long currentUserId = sessionManager.getCurrentUserId();
		User user = userDao.getUserById(currentUserId);

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new SecurityAccessException("Password is incorrect");
		}
		
		user.setPassword(passwordEncoder.encode(newPassword));
		userDao.updateUser(user);
	}

	@Override
	public void changeUserPassword(Long userId, String newPassword) {
		
		// it's better to use annotation driven security validation using spring secruity
		// or custom AOP interceptors, but use simple implementation for this test  
		if (sessionManager.getCurrentUserRole() != Role.ADMIN) {
			throw new SecurityAccessException("Access denied");
		}
		
		User user = userDao.getUserById(userId);		
		user.setPassword(passwordEncoder.encode(newPassword));
		userDao.updateUser(user);
	}
	
	@Override
	public User addUser(User user) {
		// skip validation for the sake of simplicity

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userDao.addUser(user);
	}
	
	@Override
	public void removeUser(long userId) {
		userDao.removeUser(userId);
	}

	protected String getNewRandomPassword() {
		return RandomStringUtils.random(10);
	}

	protected void sendEmailWithNewPasswordToUser(String userEmail, String password) {
		// skip implementation for the sake of simplicity
	}


}
