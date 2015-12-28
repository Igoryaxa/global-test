package com.global.test.service;

import com.global.test.model.User;

public interface UserService {
	User addUser(User user);
	void removeUser(long userId);
	void loginUser(String userEmail, String password);
	void logoutUser();
	void resetPassword(String userEmail);
	void changePassword(String oldPassword, String newPassword);
	void changeUserPassword(Long userId, String newPassword);
}
