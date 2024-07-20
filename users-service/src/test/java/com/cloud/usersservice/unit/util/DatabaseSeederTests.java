package com.cloud.usersservice.unit.util;

import com.cloud.usersservice.repository.UsersRepository;
import com.cloud.usersservice.util.DatabaseSeeder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import com.cloud.usersservice.entity.User;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class DatabaseSeederTests {
    private DatabaseSeeder databaseSeeder;
    @Mock
    private UsersRepository usersRepository;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    @BeforeEach
    public void setUp() {
        databaseSeeder = new DatabaseSeeder(usersRepository);
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
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        UUID uuid4 = UUID.randomUUID();
        UUID uuid5 = UUID.randomUUID();
        User admin = new User(uuid1, "Sultan", "Khassenov", "hasenovsultanbek@gmail.com", "fring0213", 2, List.of("superadmin", "user"));
        User secondUser = new User(uuid2, "Saule", "Koldybaeva", "sullek75@gmail.com", "sullek75", 1, List.of("user"));
        User thirdUser = new User(uuid3, "Dastan", "Khassenov", "dasthas12@gmail.com", "Pillager01", 1, List.of("user"));
        User fourthUser = new User(uuid4, "Nurakhmet", "Azhibek", "n.azhibek@mail.ru", "tokyoshymkent", 1, List.of("user"));
        User fifthUser = new User(uuid5, "Anvar", "Tolebayev", "anv.tul@gmu.edu", "hasthebest", 1, List.of("user"));
        List<User> users = List.of(admin, secondUser, thirdUser, fourthUser, fifthUser);
        when(usersRepository.saveAll(users)).thenReturn(users);
        //Act
        databaseSeeder.seedUsers();
        //Assert
        verify(usersRepository).saveAll(users);
        assertTrue(outContent.toString().contains("Seeding admin user and others..."));
    }
}
