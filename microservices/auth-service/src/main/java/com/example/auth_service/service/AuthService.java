package com.example.auth_service.service;

import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 用戶登入
     */
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        
        // 實際應用應使用 BCrypt 比對加密密碼
        if (!user.getPassword().equals(password)) {
            return null;
        }
        
        if (!user.isActive()) {
            return null;
        }
        
        // 生成 JWT Token
        return jwtUtil.generateToken(user.getUsername(), user.getId());
    }
    
    /**
     * 驗證 Token
     */
    public boolean verifyToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    /**
     * 取得 Token 資訊
     */
    public AuthTokenInfo getTokenInfo(String token) {
        if (!jwtUtil.validateToken(token)) {
            return null;
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        return new AuthTokenInfo(username, userId);
    }
    
    // 內部類別
    public static class AuthTokenInfo {
        private String username;
        private Long userId;
        
        public AuthTokenInfo(String username, Long userId) {
            this.username = username;
            this.userId = userId;
        }
        
        public String getUsername() { return username; }
        public Long getUserId() { return userId; }
    }
}