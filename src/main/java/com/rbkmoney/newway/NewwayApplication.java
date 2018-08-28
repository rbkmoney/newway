package com.rbkmoney.newway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.rbkmoney.newway", "com.rbkmoney.dbinit"})
public class NewwayApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewwayApplication.class, args);
    }

}
