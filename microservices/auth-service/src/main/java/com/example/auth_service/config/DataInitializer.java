package com.example.auth_service.config;

import com.example.auth_service.entity.AuthToken;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.AuthTokenRepository;
import com.example.auth_service.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class DataInitializer {
     
	    @Bean
	    CommandLineRunner initUserData(UserRepository userRepository) {
	        return args -> {
	            userRepository.deleteAll();
	            
	            // 建立測試用戶
	            userRepository.save(new User("john", "password123", "john@example.com"));
	            userRepository.save(new User("jane", "password456", "jane@example.com"));
	            userRepository.save(new User("admin", "admin123", "admin@example.com"));
	            
	            System.out.println("✅ Auth Service 測試用戶已建立!");
	            userRepository.findAll().forEach(user -> 
	                System.out.println("   User: " + user.getUsername() + 
	                                 " | Password: " + user.getPassword())
	            );
	        };
	    }
	
    @Bean
    CommandLineRunner initAuthData(AuthTokenRepository repository) {
        return args -> {
            // 清空舊資料
            repository.deleteAll();
            
            // 建立測試 Token
            repository.save(new AuthToken(
                "valid-token-001", 
                "user001", 
                LocalDateTime.now().plusDays(30)
            ));
            
            repository.save(new AuthToken(
                "valid-token-002", 
                "user002", 
                LocalDateTime.now().plusDays(30)
            ));
            
            repository.save(new AuthToken(
                "expired-token", 
                "user003", 
                LocalDateTime.now().minusDays(1) // 已過期
            ));
            
            System.out.println("✅ Auth Service 測試資料已建立!");
            repository.findAll().forEach(token -> 
                System.out.println("   Token: " + token.getToken() + 
                                 " | Valid: " + token.isValid())
            );
        };
    }
    public static void main(String[] args) {
        // H2 Database URL（memory 模式，可改為 file 模式）
        String url = "jdbc:h2:~/authdb;DB_CLOSE_DELAY=-1"; 
        String user = "sa";
        String password = "";

        // 日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            
            // 1️⃣ 建立資料表 (如果不存在)
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS auth_token (
                    token VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255),
                    expire_at TIMESTAMP
                )
                """;
            conn.createStatement().execute(createTableSql);
            
            // 2️⃣ 清空舊資料
            conn.createStatement().execute("DELETE FROM auth_token");

            // 3️⃣ 插入測試資料
            String insertSql = "INSERT INTO auth_token(token, username, expire_at) VALUES(?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                
                stmt.setString(1, "valid-token-001");
                stmt.setString(2, "user001");
                stmt.setString(3, LocalDateTime.now().plusDays(30).format(formatter));
                stmt.executeUpdate();

                stmt.setString(1, "valid-token-002");
                stmt.setString(2, "user002");
                stmt.setString(3, LocalDateTime.now().plusDays(30).format(formatter));
                stmt.executeUpdate();

                stmt.setString(1, "expired-token");
                stmt.setString(2, "user003");
                stmt.setString(3, LocalDateTime.now().minusDays(1).format(formatter));
                stmt.executeUpdate();
            }

            System.out.println("✅ 測試資料建立完成!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ 資料初始化失敗: " + e.getMessage());
        }
    }
}