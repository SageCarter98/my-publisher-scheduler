package com.mps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MpsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MpsApplication.class, args);
    }
}
