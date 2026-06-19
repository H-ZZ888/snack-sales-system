package com.agricultural.interceptor;

import com.agricultural.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 可选JWT拦截器
 * 用于那些登录可选的接口，如果有token就验证并设置userId，没有token也放行
 */
@Component
public class OptionalJwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 获取Token
        String token = request.getHeader(header);
        
        // 如果没有token，直接放行（userId为null）
        if (token == null || token.isEmpty()) {
            return true;
        }

        // 去除Bearer前缀
        if (token.startsWith(prefix)) {
            token = token.substring(prefix.length()).trim();
        }

        // 验证Token
        try {
            if (jwtUtil.validateToken(token)) {
                // Token有效，将用户信息存入request
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String userType = jwtUtil.getUserTypeFromToken(token);
                
                request.setAttribute("userId", userId);
                request.setAttribute("username", username);
                request.setAttribute("userType", userType);
            }
        } catch (Exception e) {
            // Token无效，但仍然放行（userId为null）
            // 用户可以访问接口，但看不到收藏等需要登录的信息
        }

        return true;
    }
}

