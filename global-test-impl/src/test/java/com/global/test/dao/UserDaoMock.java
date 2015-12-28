package com.global.test.dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class UserDaoMock extends AbstractInMemoryUserDao {

}
