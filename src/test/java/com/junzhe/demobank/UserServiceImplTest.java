package com.junzhe.demobank;

import com.junzhe.demobank.models.Receipt;
import com.junzhe.demobank.models.Session;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.operations.OperationName;
import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.User;
import com.junzhe.demobank.models.user.UserInfo;
import com.junzhe.demobank.models.user.UserPayload;
import com.junzhe.demobank.repository.UserRepository;
import com.junzhe.demobank.services.UserService;
import com.junzhe.demobank.services.UserServiceImpl;
import com.junzhe.demobank.session.SessionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl service;
    @Mock
    UserRepository repository;
    @Mock
    SessionManager manager;

    @Test
    public void testGetCurrentUserInfo() {
        User user = new User("test", "pass");
        JwtUser jwtUser = new JwtUser("id", "test");
        when(repository.getCurrentUserInfo(jwtUser)).thenReturn(user);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        UserInfo userInfo = service.getCurrentUserInfo();
        assertEquals(userInfo.getUsername(), user.getUsername());
        verify(repository, times(1)).getCurrentUserInfo(jwtUser);
        verify(manager, times(1)).getCurrentUser();
    }

    @Test
    public void testCreateUser() {
        UserPayload payload = new UserPayload();
        payload.setUsername("test"); payload.setPassword("pass");
        User user = new User("test", "pass");
        when(repository.createUser(payload)).thenReturn(user);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        JwtUser jwtUser = service.createUser(payload, request, response);
        assertEquals(jwtUser.getUsername(), payload.getUsername());
        verify(repository, times(1)).createUser(payload);
    }

    @Test
    public void testLogin() {
        UserPayload payload = new UserPayload();
        payload.setUsername("test"); payload.setPassword("pass");
        User user = new User("test", "pass");
        when(repository.login(payload)).thenReturn(user);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        JwtUser jwtUser = service.login(payload, request, response);
        assertEquals(jwtUser.getUsername(), payload.getUsername());
        verify(repository, times(1)).login(payload);
    }

    @Test
    public void testDeposit() {
        double amount = 300;
        JwtUser jwtUser = new JwtUser("id", "test");
        User user = new User("test", "pass"); user.setBalance(300);
        when(manager.getCurrentUser()).thenReturn(jwtUser);
        when(repository.update(amount, jwtUser)).thenReturn(user);
        when(manager.getSession()).thenReturn(new Session());
        Receipt receipt = service.deposit(amount);
        assertEquals(receipt.getUsername(), jwtUser.getUsername());
        assertEquals(receipt.getAmount(), user.getBalance());
        assertEquals(receipt.getOp(), OperationName.DEPOSIT);
        verify(repository, times(1)).update(amount, jwtUser);
        verify(manager, times(1)).getCurrentUser();
        verify(manager, times(2)).getSession();
    }

    @Test
    public void testGetOperations () {
        List<Operation> ops = new ArrayList<>();
        Operation op1 = new Operation(OperationName.LOG_IN, "id");
        Operation op2 = new Operation(OperationName.DEPOSIT, "id");
        Operation op3 = new Operation(OperationName.WITHDRAW, "id");
        ops.add(op1); ops.add(op2); ops.add(op3);
        when(manager.getOperations(1)).thenReturn(ops);
        List<Operation> res = service.getOperations(1);
        assertEquals(ops.size(), res.size());
        verify(manager, times(1)).getOperations(1);
    }

}
