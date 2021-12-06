package com.junzhe.demobank.repository;

import com.junzhe.demobank.models.user.JwtUser;
import com.junzhe.demobank.models.user.User;
import com.junzhe.demobank.models.user.UserPayload;
import com.junzhe.demobank.utils.StringUtil;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class UserRepository {

    private Map<String, User> users;

    public UserRepository() {
        this.users = new HashMap<>();
    }

    public User getCurrentUserInfo(JwtUser current) {
        return users.getOrDefault(current.getUsername(), null);
    }

    public User createUser(UserPayload payload) {
        if (users.containsKey(payload.getUsername())) {
            throw new InternalException("User already exists, please log in");
        }
        User user = new User(payload.getUsername(), StringUtil.encryptPW(payload.getPassword()));
        users.put(user.getUsername(), user);
        return user;
    }

    public User login(UserPayload payload) {
        if (users.containsKey(payload.getUsername())
                && StringUtil.isPwCorrect(payload.getPassword(), users.get(payload.getUsername()).getPassword())) {
            return users.get(payload.getUsername());
        }
        throw new IllegalArgumentException("username or password error");
    }

    public User update(double amount, JwtUser current) {
        User user = users.getOrDefault(current.getUsername(), null);
        if (user == null) {
            throw new NoSuchElementException("No user found");
        }
        if (user.getBalance() + amount < 0) {
            throw new IllegalArgumentException("Input amount error");
        }
        user.setBalance(user.getBalance() + amount);
        return user;
    }
}
