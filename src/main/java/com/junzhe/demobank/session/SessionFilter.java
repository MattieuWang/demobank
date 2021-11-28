package com.junzhe.demobank.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.junzhe.demobank.models.operations.Operation;
import com.junzhe.demobank.models.operations.OperationName;
import com.junzhe.demobank.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionFilter implements Filter {

    @Autowired
    SessionManager sessionManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        if (httpRequest.getServletPath().startsWith("/auth") && !httpRequest.getServletPath().contains("logout")) {
            filterChain.doFilter(servletRequest, servletResponse);
            logOperations(httpRequest, httpResponse);
            return;
        }
        if (sessionManager.getCurrentUser()==null || !isAccessAllowed(httpRequest, httpResponse)) {
            setResponse(httpResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
        logOperations(httpRequest, httpResponse);
    }

    private boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = getAccessToken(request);
        if (token != null && CookieUtils.getSessionClaim(token) != null) {
            Claims claims = CookieUtils.getSessionClaim(token);
            return !CookieUtils.isExpired(claims) && claims.getSubject().equals(sessionManager.getCurrentUser().getUsername());
        }

        return false;
    }

    private String getAccessToken(HttpServletRequest request) {
        return CookieUtils.getCookie(request, SessionManager.TOKEN);
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        Map<String, String> result = new HashMap<>();
        result.put("message", "You're not authorized");
        result.put("code", "401");
        PrintWriter writer = response.getWriter();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        writer.write(ow.writeValueAsString(result));
        writer.flush();
        writer.close();
    }

    private void logOperations(HttpServletRequest request, HttpServletResponse response) {
        if (request.getServletPath().contains("logout"))
            return;
        Logger logger = LoggerFactory.getLogger(SessionFilter.class);
        if (response.getStatus() == 200) {
            Operation op = new Operation();
            String logMsg = "Request URL:" + request.getRequestURL().toString() + "  ";
            String user_id = sessionManager.getCurrentUser().getId();
            op.setUser_id(user_id);
            if (request.getServletPath().startsWith("/auth")) {
                op.setOperationName(OperationName.LOG_IN);
            } else if (request.getServletPath().contains("deposit")) {
                op.setOperationName(OperationName.DEPOSIT);
                op.setReceipt(sessionManager.getSession().getReceipt());
            } else if (request.getServletPath().contains("withdraw")) {
                op.setOperationName(OperationName.WITHDRAW);
                op.setReceipt(sessionManager.getSession().getReceipt());
            }
            sessionManager.refreshSession(request, response);
            if (op.getOperationName() != null) {
                sessionManager.addOperation(op);
                logMsg += op.toString();
                logger.info(logMsg);
            }
        }
    }


}
