package com.global.test.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.global.test.dao.UserDao;
import com.global.test.exception.SecurityAccessException;
import com.global.test.exception.UserNotExistException;
import com.global.test.model.User;
import com.global.test.session.SessionManager;

@RunWith(MockitoJUnitRunner.class)
public class DefaultUserServiceTest {
	@InjectMocks
	private DefaultUserService service;
	
	@Mock
	private SessionManager sessionManager;
	
	@Mock
	private UserDao userDao;

	@Mock
    private PasswordEncoder passwordEncoder;

	private User user;
	
	@Before
	public void init() {
		user = new User();
		user.setEmail("email@email.com");
		user.setPassword("encodedPassword");
	}

	@Test
	public void testLoginUser() {
		when(userDao.getUserByEmail(user.getEmail())).thenReturn(user);
		when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);
		
		service.loginUser(user.getEmail(), "password");
		
		verify(sessionManager).loginUser(user);
	}

	@Test(expected = UserNotExistException.class)
	public void testLoginUserWhenNoUserByEmailExist() {
		service.loginUser(user.getEmail(), "password");
	}

	@Test(expected = SecurityAccessException.class)
	public void testLoginUserWhenPasswordsNotMatched() {
		when(userDao.getUserByEmail(user.getEmail())).thenReturn(user);
		when(passwordEncoder.matches("password", user.getPassword())).thenReturn(false);
		
		service.loginUser(user.getEmail(), "password");
	}
	
	@Test
	public void testResetPassword() {
		DefaultUserService spyService = spy(service);

		when(userDao.getUserByEmail(user.getEmail())).thenReturn(user);
		when(passwordEncoder.encode("password")).thenReturn("newEncodedPassword");
		when(spyService.getNewRandomPassword()).thenReturn("newPassword");
		doNothing().when(spyService).sendEmailWithNewPasswordToUser(anyString(), anyString());
		
		spyService.resetPassword(user.getEmail());
		
		user.setPassword("newEncodedPassword");
		
		verify(spyService).getNewRandomPassword();
		verify(spyService).sendEmailWithNewPasswordToUser(user.getEmail(), "newPassword");
		verify(userDao).updateUser(user);
	}
}
