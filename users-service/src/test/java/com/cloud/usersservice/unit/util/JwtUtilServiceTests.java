package com.cloud.usersservice.unit.util;

import com.cloud.usersservice.util.JwtUtilService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class JwtUtilServiceTests {
    @Autowired
    private JwtUtilService jwtUtilService;
    static Stream<Arguments> getTokenAndExpectedRoles(){
        return Stream.of(
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsInJvbGVzIjoiYWRtaW4sdXNlciIsImV4cCI6MTc0OTY0ODMzOCwiaWF0IjoxNzE4MTEyMDk4fQ.92i194lJKXrppTO2ptH3Q8ex-YcublFiPiebddgqAos", "user"),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsInJvbGVzIjoiYWRtaW4iLCJleHAiOjE3NDk2NDg3MzYsImlhdCI6MTcxODExMjczNn0.mBYtpElI8WAItpnDRKcz8Kf0UjglAaL34s3K9gprsbA", "admin"),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsInJvbGVzIjoidXNlcixhZG1pbiIsImV4cCI6MTc0OTY0ODczNiwiaWF0IjoxNzE4MTEyNzM2fQ.gVJFHsQUZhv4rZTuHUMYZj7s5B0pZH5G7YdX7DrRYPM", "user,admin")
        );
    }


    @ParameterizedTest
    @MethodSource("getTokenAndExpectedRoles")
    public void jwtService_getRolesFromToken_returns_valid_roles(String token, String expectedRoles){
        String actualRoles = jwtUtilService.getRolesFromToken(token);
        assertEquals(expectedRoles, actualRoles);
    }
    static Stream<Arguments> getTokenAndExpectedSubjects(){
        return Stream.of(
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTdWx0YW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJyb2xlcyI6ImFkbWluIiwiZXhwIjoxNzQ5NjUxODEwLCJpYXQiOjE3MTgxMTU4MTB9.f80qGRYRGl6dUt1H0tj4-oL5pmsKZGjnscPT-O5F_JI", "Sultan"),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYXVsZSIsIklzc3VlciI6Iklzc3VlciIsInJvbGVzIjoidXNlciIsImV4cCI6MTc0OTY1MTgxMCwiaWF0IjoxNzE4MTE1ODEwfQ.F0pFa80H27dA550-Py7_Pe4QoSnu_eoF3sXnrHrxj2c", "Saule"),
                Arguments.of("eyJhbGciOiJIUzI1NiJ9.eyJJc3N1ZXIiOiJJc3N1ZXIiLCJyb2xlcyI6InVzZXIsYWRtaW4iLCJleHAiOjE3NDk2NTE4MTAsImlhdCI6MTcxODExNTgxMH0.v5eb9v08zFsBZa0l1dw6w3GIMOmqP3lB2nU4DL0DZcY", null)
        );
    }
    @ParameterizedTest
    @MethodSource("getTokenAndExpectedSubjects")
    public void jwtService_getUserDetailsFromToken_returns_valid_user_details(String token, String expectedSubject) {
        var roles = jwtUtilService.getRolesFromToken(token);
        UserDetails userDetails = jwtUtilService.getUserDetailsFromToken(token, roles);
        assertEquals(expectedSubject, userDetails.getUsername());
    }
}
