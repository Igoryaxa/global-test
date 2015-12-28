package com.global.test.dao;

import com.global.test.model.User;

public interface UserDao {
	User getUserById(Long id);

	User getUserByEmail(String email);

	User addUser(User user);

	void updateUser(User user);

	boolean removeUser(Long id);
}
