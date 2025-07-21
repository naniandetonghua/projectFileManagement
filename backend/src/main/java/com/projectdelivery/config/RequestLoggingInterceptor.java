package com.projectdelivery.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String REQUEST_ID = "requestId";
    private static final String START_TIME = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        long startTime = System.currentTimeMillis();
        
        request.setAttribute(REQUEST_ID, requestId);
        request.setAttribute(START_TIME, startTime);
        
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String userAgent = request.getHeader("User-Agent");
        String remoteAddr = getClientIpAddress(request);
        
        logger.info("[{}] 收到请求 - 方法: {}, URI: {}, 查询参数: {}, 用户代理: {}, 客户端IP: {}", 
                   requestId, method, uri, queryString, userAgent, remoteAddr);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        Long startTime = (Long) request.getAttribute(START_TIME);
        
        if (requestId != null && startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            
            if (ex != null) {
                logger.error("[{}] 请求处理异常 - 方法: {}, URI: {}, 状态码: {}, 耗时: {}ms, 异常: {}", 
                           requestId, method, uri, status, duration, ex.getMessage(), ex);
            } else {
                logger.info("[{}] 请求处理完成 - 方法: {}, URI: {}, 状态码: {}, 耗时: {}ms", 
                           requestId, method, uri, status, duration);
            }
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 