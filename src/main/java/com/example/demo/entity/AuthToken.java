package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "auth_token")
@Data
public class AuthToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    private Integer userId;
}
