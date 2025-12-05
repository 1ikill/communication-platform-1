package com.sdc.whatsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sdc.whatsapp")
public class WhatsappServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WhatsappServiceApplication.class, args);
    }
}
