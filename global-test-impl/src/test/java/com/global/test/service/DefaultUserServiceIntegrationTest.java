package com.global.test.service;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.global.test.dao.UserDao;
import com.global.test.exception.SecurityAccessException;
import com.global.test.model.Role;
import com.global.test.model.User;

@ContextConfiguration(locations = { "/application-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class DefaultUserServiceIntegrationTest {
	@Autowired
	private UserService userService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private User adminUser;
	private String adminUserPassword = "MonicaPassword"; 
	
	private User usualUser;
	private String usualUserPassword = "JosephPassword"; 

	@Before
	public void init() {
		adminUser = new User();
		adminUser.setRole(Role.ADMIN);
		adminUser.setFirstName("Monica");
		adminUser.setEmail("monika@domain.com");
		adminUser.setPassword(adminUserPassword);
		
		adminUser = userService.addUser(adminUser);

		usualUser = new User();
		usualUser.setRole(Role.USER);
		usualUser.setFirstName("Joseph");
		usualUser.setPassword("JosephPassword");
		usualUser.setEmail("joseph@domain.com");
		
		usualUser = userService.addUser(usualUser);
	}

	@After
	public void tearDown() {
		userService.removeUser(adminUser.getId());
		userService.removeUser(usualUser.getId());
	}
	
	@Test
	public void testChangeUserPassword() {
		userService.loginUser(adminUser.getEmail(), adminUserPassword);
		
		String newPassword = "" + System.currentTimeMillis();
		userService.changeUserPassword(usualUser.getId(), newPassword);
		
		User updatedUser = userDao.getUserById(usualUser.getId());
		assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
	}

	@Test(expected = SecurityAccessException.class)
	public void testChangeUserPasswordByAnonymUser() {
		userService.logoutUser();
		
		userService.changeUserPassword(usualUser.getId(), "");
	}
	
	@Test(expected = SecurityAccessException.class)
	public void testChangeUserPasswordByRoleOtherThanAdmin() {
		userService.logoutUser();
		userService.loginUser(usualUser.getEmail(), usualUserPassword);
		
		userService.changeUserPassword(usualUser.getId(), "");
	}

}
