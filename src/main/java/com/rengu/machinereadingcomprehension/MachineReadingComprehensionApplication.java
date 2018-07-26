package com.rengu.machinereadingcomprehension;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MachineReadingComprehensionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MachineReadingComprehensionApplication.class, args);
    }
}
