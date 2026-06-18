package com.lookator;

import com.lookator.analysis.ScoringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ScoringProperties.class)
public class LookatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(LookatorApplication.class, args);
    }
}
