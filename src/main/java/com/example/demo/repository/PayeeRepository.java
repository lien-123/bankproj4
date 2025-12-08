package com.example.demo.repository;

import com.example.demo.entity.Payee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayeeRepository extends JpaRepository<Payee, Integer> {
    List<Payee> findByUserId(Integer userId);
}
