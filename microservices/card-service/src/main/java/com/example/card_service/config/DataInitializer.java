package com.example.card_service.config;

import com.example.card_service.entity.Card;
import com.example.card_service.repository.CardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Configuration
public class DataInitializer {
	private static final String JDBC_URL = "jdbc:h2:~/carddb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
	private static final String USER = "sa";
	private static final String PASSWORD = "";  
    
	@Bean
    CommandLineRunner initCardData(CardRepository repository) {
        return args -> {
            repository.deleteAll();
            
            repository.save(new Card(
                "1234-5678-9012-3456", 
                "John Doe", 
                new BigDecimal("50000.00")
            ));
            
            repository.save(new Card(
                "9876-5432-1098-7654", 
                "Jane Smith", 
                new BigDecimal("100000.00")
            ));
            
            repository.save(new Card(
                "1111-2222-3333-4444", 
                "Poor User", 
                new BigDecimal("100.00") // 餘額不足
            ));
            
            System.out.println("✅ Card Service 測試資料已建立!");
            repository.findAll().forEach(card -> 
                System.out.println("   Card: " + card.getCardNo() + 
                                 " | Balance: $" + card.getBalance())
            );
        };
    }
	public static void main(String[] args) {

        System.out.println("🚀 Initializing Card Service test data...");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // 1️⃣ 建立 Table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS card (
                        card_no VARCHAR(30) PRIMARY KEY,
                        owner VARCHAR(100),
                        balance DECIMAL(19,2)
                    )
                """);

                stmt.execute("DELETE FROM card");
            }

            // 2️⃣ Insert 測試資料
            String sql = "INSERT INTO card (card_no, owner, balance) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                insert(ps, "1234-5678-9012-3456", "John Doe", new BigDecimal("50000.00"));
                insert(ps, "9876-5432-1098-7654", "Jane Smith", new BigDecimal("100000.00"));
                insert(ps, "1111-2222-3333-4444", "Poor User", new BigDecimal("100.00"));
            }

            System.out.println("✅ Card Service 測試資料已建立完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insert(
            PreparedStatement ps,
            String cardNo,
            String owner,
            BigDecimal balance
    ) throws Exception {

        ps.setString(1, cardNo);
        ps.setString(2, owner);
        ps.setBigDecimal(3, balance);
        ps.executeUpdate();

        System.out.println("   ✔ Card: " + cardNo + " | Balance: $" + balance);
    }
}