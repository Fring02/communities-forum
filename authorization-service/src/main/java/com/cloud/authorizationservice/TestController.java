/*
package com.cloud.authorizationservice;

import com.cloud.authorizationservice.client.UsersClient;
import com.cloud.authorizationservice.service.HystrixService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    */
/*private final RestTemplate template;
    @Value("${users-service-url}")
    private String url;*//*

    private final UsersClient client;
    private final HystrixService service;
    public TestController(UsersClient client, HystrixService service) {
        this.client = client;
        this.service = service;
    }
    @GetMapping(value = "/user")
    public String get(){
        return client.getUser();
    }
    @GetMapping(value = "/hystrix")
    public String hystrix(){
        return service.hystrix();
    }
}
*/
