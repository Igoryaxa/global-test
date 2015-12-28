package com.global.test.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;

import com.global.test.model.User;

public abstract class AbstractInMemoryUserDao implements UserDao {
	private  Collection<User> users;
	
	private long nextId = 1;
	
	@PostConstruct
	private void initUsers() {
		users = loadUsers();

		Optional<User> userWithMaxId = users.stream().max((u1, u2) -> u1.getId().compareTo(u2.getId()));
		if (userWithMaxId.isPresent()) {
			nextId = userWithMaxId.get().getId() + 1;
		}
	}
			
	public User getUserById(Long id) {
		return cloneUser(users
				.stream()
				.filter((u) -> u.getId().equals(id))
				.findFirst()
				.orElse(null));
	}

	public User getUserByEmail(String email) {
		return cloneUser(users
				.stream()
				.filter((u) -> u.getEmail().equals(email))
				.findFirst()
				.orElse(null));
	}

	public User addUser(User user) {
		user.setId(nextId++);
		users.add(cloneUser(user));
		return user;
	}

	@Override
	public void updateUser(User user) {
		removeUser(user.getId());
		users.add(cloneUser(user));
	}
	
	public boolean removeUser(Long id) {
		return users.removeIf((u) -> u.getId().equals(id));
	}

	protected Collection<User> getUsers() {
		return users;
	}

	protected Collection<User> loadUsers() {
		return new LinkedList<>();
	}

	private User cloneUser(User user) {
		if (user == null) {
			return null;
		}
		return (User)SerializationUtils.deserialize(SerializationUtils.serialize(user));
	}
}
