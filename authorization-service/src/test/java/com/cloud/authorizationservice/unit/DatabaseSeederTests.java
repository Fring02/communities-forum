package com.cloud.authorizationservice.unit;

import com.cloud.authorizationservice.entity.LoginInfo;
import com.cloud.authorizationservice.repository.LoginsRepository;
import com.cloud.authorizationservice.util.DatabaseSeeder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class DatabaseSeederTests {
    private DatabaseSeeder databaseSeeder;
    @Mock
    private LoginsRepository loginsRepository;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    @BeforeEach
    public void setUp() {
        databaseSeeder = new DatabaseSeeder(loginsRepository);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    @AfterEach
    public void tearDown(){
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    @Test
    public void testSeedUsers() {
        //Arrange
        var admin = new LoginInfo("fring0213", "casper2001");
        var secondUser = new LoginInfo("Pillager01", "casper2001");
        List<LoginInfo> users = List.of(admin, secondUser);
        when(loginsRepository.saveAll(users)).thenReturn(users);
        //Act
        databaseSeeder.seedUsers();
        //Assert
        verify(loginsRepository).saveAll(users);
        assertTrue(outContent.toString().contains("Seeding admin user and others..."));
    }
}
