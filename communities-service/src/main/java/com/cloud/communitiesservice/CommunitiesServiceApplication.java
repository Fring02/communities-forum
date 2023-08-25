package com.cloud.communitiesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CommunitiesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunitiesServiceApplication.class, args);
    }

}
