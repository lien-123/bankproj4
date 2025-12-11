package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/menu/")
    public String menu() {
        return "menu";
    }

    @GetMapping("/balance/")
    public String balance() {
        return "balance";
    }

    @GetMapping("/transaction/")
    public String transaction() {
        return "transaction";
    }

    @GetMapping("/transfer/")
    public String transfer() {
        return "transfer";
    }

    @GetMapping("/register/")
    public String register() {
        return "register"; 
    }

    @GetMapping("/notification/")
    public String notification() {
        return "notification";
    }
}