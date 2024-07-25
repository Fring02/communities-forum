package com.cloud.servicediscovery.integration;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
public class DiscoveryServerTests {
    @Container
    private final GenericContainer<?> server = new GenericContainer<>("fring02/communities-forum:service-discovery:latest")
            .withExposedPorts(5001);
    @Autowired
    private EurekaClientConfigBean eurekaClientConfigBean;
    @Test
    public void testDiscoveryServer() throws InterruptedException {
        try{
            String url = "http://" + server.getHost() + ":" + server.getMappedPort(5001) + "/eureka/";
            eurekaClientConfigBean.setServiceUrl(Map.of("defaultZone", url));
            Thread.sleep(5000);
            RestTemplate restTemplate = new RestTemplate();
            var headers = new HttpHeaders();
            headers.setBasicAuth("discUser", "discPassword");
            ResponseEntity<String> response = restTemplate.exchange(url + "apps", HttpMethod.GET, new HttpEntity<>(headers), String.class);
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            System.out.println(response.getBody());
        } finally {
            server.close();
        }
    }
}
