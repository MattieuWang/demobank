package com.junzhe.demobank.session;

import com.junzhe.demobank.models.JwtUser;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.Session;
import com.junzhe.demobank.utils.CookieUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SessionManager {


    private Map<String, Session> sessions;
    private Session currentSession;

    private List<Operation> operations;
    private Map<String, List<String>> user_op;

    public final static String TOKEN = "session_id";

    public SessionManager() {
        this.currentSession = new Session();
        sessions = new HashMap<>();
        this.operations = new ArrayList<>();
        this.user_op = new HashMap<>();
    }

    public Session getSession() {
        return currentSession;
    }

    public String setSessionUser(JwtUser user, HttpServletRequest request, HttpServletResponse response) {
        String ip = request.getRemoteAddr();
        currentSession = sessions.getOrDefault(ip, new Session());
        String jwt = getJwt(request);
        jwt = CookieUtils.generateJWT(user);
        CookieUtils.addCookie(TOKEN, jwt, "/", request, response);
        currentSession.setCurrent(user);
        return jwt;
    }

    public void refreshSession(HttpServletRequest request, HttpServletResponse response) {
        String jwt = CookieUtils.generateJWT(getCurrentUser());
        CookieUtils.addCookie(TOKEN, jwt, "/", request, response);
    }

    public boolean logout(HttpServletResponse response) {
        CookieUtils.removeCookie(TOKEN, "/", response);
        this.currentSession.setCurrent(null);
        return true;
    }

    public JwtUser getCurrentUser() {
        return currentSession.getCurrent();
    }

    public String getJwt(HttpServletRequest request) {
        return CookieUtils.getCookie(request, TOKEN);
    }

    public List<Operation> getOperations(int num) {
        List<String> op_ids = user_op.getOrDefault(currentSession.getCurrent().getId(), new ArrayList<>());
        List<Operation> ops = operations.stream().filter(op -> op_ids.contains(op.getId())).toList();
        int size = ops.size() / 5;
        int start = Math.min(num - 1, size) * 5;
        int end = Math.min(start + 5, ops.size());
        return ops.subList(start, end);
    }

    public void addOperation(Operation op) {
        operations.add(op);
        List<String> tmp = user_op.getOrDefault(currentSession.getCurrent().getId(), new ArrayList<>());
        tmp.add(op.getId());
        user_op.put(currentSession.getCurrent().getId(), tmp);
    }

}
