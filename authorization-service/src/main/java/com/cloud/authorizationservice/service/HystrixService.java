package com.cloud.authorizationservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class HystrixService {
    private final RestTemplate template;
    public HystrixService(RestTemplate template) {
        this.template = template;
    }
    //@HystrixCommand(fallbackMethod = "fallback")
    public String hystrix(){
        var response = template.getForObject("http://users-service/random_phrase", String.class);
        return response + " HYSTRIX";
    }
    public String fallback(){
        return "Fallback occured";
    }
}
