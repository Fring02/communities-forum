package com.cloud.postsservice.util;

import com.cloud.postsservice.exception.EntityNotFoundException;
import com.cloud.postsservice.exception.UserNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
@Component
public class UsersRestClient {
    private final RestTemplate template;
    public UsersRestClient(RestTemplate template) {
        this.template = template;
    }
    public boolean userExists(UUID userId) throws EntityNotFoundException {
        var response = template.getForEntity("http://users-service/api/users/{id}/exists", Boolean.class, userId);
        if(response.getStatusCode().is4xxClientError()) throw new UserNotFoundException("User id is invalid");
        return response.getBody();
    }
}
