package com.ziwei.pomodoro.interceptor;

import com.ziwei.pomodoro.common.BaseContext;
import com.ziwei.pomodoro.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws IOException {
        String token = request.getHeader("Authorization");
        if (token == null) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(401);
            response.getWriter().write("未登录或token已失效");
            return false;
        }
        Long userId = JwtUtil.parseToken(token);
        BaseContext.setCurrentId(userId);
        return true;
    }
}
