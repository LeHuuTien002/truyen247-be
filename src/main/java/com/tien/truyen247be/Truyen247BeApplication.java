package com.tien.truyen247be;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Truyen247BeApplication {

    public static void main(String[] args) {
        // Đặt các biến môi trường
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir")) // Chỉ định thư mục chứa .env
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        SpringApplication.run(Truyen247BeApplication.class, args);
    }

}
