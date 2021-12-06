package com.junzhe.demobank;

import com.junzhe.demobank.repository.UserRepository;
import com.junzhe.demobank.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

public class UserServiceImplTest {

    @MockBean
    private UserRepository repository;
    private UserService userService;

    @Test
    public void testGetCurrentUserInfo() {
        //Mockito.when(repository.)
    }

}
