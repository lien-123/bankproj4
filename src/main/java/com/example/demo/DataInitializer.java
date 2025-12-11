package com.example.demo;

import com.example.demo.entity.Account;
import com.example.demo.entity.Payee;
import com.example.demo.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.PayeeRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            AccountRepository accountRepo,
            PayeeRepository payeeRepo,
            PasswordEncoder encoder
    ) {
        return args -> {

            if (!userRepository.existsByUsername("testuser")) {

                // 建立使用者
                User user = new User();
                user.setUsername("testuser");
                user.setPassword(encoder.encode("123456"));
                userRepository.save(user);

                System.out.println("建立使用者：testuser / 123456");

                // 建立帳號
                Account acc = new Account();
                acc.setUserId(user.getId());
                acc.setAccountNumber("A0001");
                acc.setBalance(new BigDecimal("10000000"));
                accountRepo.save(acc);

                System.out.println("💰 建立帳戶 A0001（餘額 $10000000）");

            //建立 Alice 的 Account
            Account acc2 = new Account();
            acc2.setUserId(user.getId());
            acc2.setAccountNumber("B0001");
            acc2.setBalance(new BigDecimal("50000"));
            accountRepo.save(acc2);

            //建立 Bob 的 Account
            Account acc3 = new Account();
            acc3.setUserId(user.getId());
            acc3.setAccountNumber("B0002");
            acc3.setBalance(new BigDecimal("50000"));
            accountRepo.save(acc3);

            // 建立 Payee
            Payee p1 = new Payee();
            p1.setUserId(user.getId());
            p1.setName("Alice");
            p1.setAccountNumber("B0001");
            payeeRepo.save(p1);

            Payee p2 = new Payee();
            p2.setUserId(user.getId());
            p2.setName("Bob");
            p2.setAccountNumber("B0002");
            payeeRepo.save(p2);

                System.out.println("建立預設約定帳戶：Alice(B0001), Bob(B0002)");

                System.out.println("✔ 初始化資料建立完成！");
            }
        };
    }
}
