package com.cloud.usersservice.controller;

import com.cloud.usersservice.entity.User;
import com.cloud.usersservice.repository.UsersRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TestController {
    @Autowired
    private UsersRepository repository;
    /*@Autowired
    private DiscoveryClient client;*/
    @GetMapping(value = "/random_phrase", produces = MediaType.TEXT_PLAIN_VALUE)
    public String s(){
        User u = repository.save(new User());
        return u.toString();
    }

    /*@GetMapping(value = "/discovery")
    public List<String> getServices(){
        var instances = client.getInstances("users-service");
        return instances.stream().map(Object::toString).collect(Collectors.toList());
    }*/
}
