package com.cloud.usersservice.integration.service;

import com.cloud.usersservice.UsersServiceApplication;
import com.cloud.usersservice.dto.user.UserCreateDto;
import com.cloud.usersservice.dto.user.UserCreatedDto;
import com.cloud.usersservice.dto.user.UserUpdateDto;
import com.cloud.usersservice.entity.User;
import com.cloud.usersservice.repository.UsersRepository;
import com.cloud.usersservice.service.UsersService;
import com.netflix.discovery.converters.Auto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = UsersServiceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class UsersServiceTests {
    private static List<String> testUserRoles = List.of("user");
    private static UUID testId;
    @Autowired
    private UsersService usersService;
    @Autowired
    private UsersRepository repository;
    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        List<User> testUsers = new ArrayList<>(10);
        for (int i = 1; i <= 9; i++) {
            User user = new User();
            user.setUserName("user" + i);
            user.setEmail("user" + i + "@gmail.com");
            user.setFirstName("Firstname " + i);
            user.setLastName("Lastname " + i);
            user.setRoles(testUserRoles);
            testUsers.add(user);
        }
        User user = new User();
        user.setUserName("fring01");
        user.setEmail("fring01@gmail.com");
        user.setFirstName("Sultanbek");
        user.setLastName("Khassenov");
        user.setRoles(testUserRoles);
        user = repository.save(user);
        testId = user.getId();
        repository.saveAll(testUsers);
    }
    @Test
    public void test_find_all() {
        var users = usersService.getAll();
        assertFalse(users.isEmpty());
        assertEquals(10, users.size());
    }
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void test_find_all_with_pages(int page){
        int count = 5;
        var users = usersService.getAll(page, count);
        assertTrue(users.hasContent());
        assertEquals(9, users.getTotalElements());
        var content = users.getContent();
        content.forEach(u -> System.out.println(u.getUserName()));
        if (page == 1) {
            assertEquals("user1", content.get(0).getUserName());
            assertEquals("user5", content.get(count - 1).getUserName());
        } else {
            assertEquals("user6", content.get(0).getUserName());
            assertEquals("user9", content.get(content.size() - 1).getUserName());
        }
    }
    @Test
    public void test_find_by_id_returns_empty(){
        UUID userId = new UUID(0, 0);
        var user = usersService.getById(userId);
        assertTrue(user.isEmpty());
    }
    @Test
    public void test_find_by_id_returns_user(){
        var userOpt = usersService.getById(testId);
        assertTrue(userOpt.isPresent());
        assertEquals("fring01", userOpt.get().getUserName());
    }
    @Test
    public void test_exists_by_id_returns_true(){
        boolean isPresent = usersService.existsById(testId);
        assertTrue(isPresent);
    }
    @Test
    public void test_exists_by_id_returns_false(){
        boolean isEmpty = usersService.existsById(new UUID(0, 0));
        assertFalse(isEmpty);
    }
    @Test
    public void test_get_karma_by_id_returns_one_karma(){
        int karma = usersService.getKarmaById(testId);
        assertEquals(1, karma);
    }
    @Test
    public void test_get_karma_by_id_throws_ENFE(){
        UUID userId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> usersService.getKarmaById(userId));
    }
    @Test
    public void test_get_by_ids_returns_empty_list(){
        var users = usersService.getByIds(List.of());
        assertTrue(users.isEmpty());
    }
    @Test
    public void test_get_by_ids_returns_users(){
        var users = usersService.getByIds(List.of(testId));
        assertFalse(users.isEmpty());
        users.forEach(u -> System.out.println(u.getUserName()));
        assertTrue(users.stream().anyMatch(u -> u.getUserName().equals("fring01")));
    }
    @Test
    public void test_get_by_ids_returns_empty() {
        var users = usersService.getByIds(List.of(UUID.randomUUID(), UUID.randomUUID()));
        assertTrue(users.isEmpty());
    }
    @Test
    public void test_get_roles_by_username_throws_IAE(){
        String username = null;
        assertThrows(IllegalArgumentException.class, () -> usersService.getRolesByUsername(username));
    }
    @Test
    public void test_get_roles_by_username_returns_empty(){
        String username = "random_username";
        var roles = usersService.getRolesByUsername(username);
        assertTrue(roles.isEmpty());
    }
    @Test
    public void test_get_roles_by_username_returns_roles(){
        String username = "fring01";
        var roles = usersService.getRolesByUsername(username);
        assumeTrue(roles.isPresent());
        assertTrue(roles.get().getRoles().contains("user"));
    }
    @Test
    public void test_delete_by_id_throws_IAE(){
        UUID id = new UUID(0, 0);
        assertThrows(IllegalArgumentException.class, () -> usersService.deleteById(id));
    }
    @Test
    public void test_delete_by_id_throws_ENFE(){
        UUID id = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> usersService.deleteById(id));
    }
    @Test
    public void test_delete_by_id_deletes(){
        UUID id = testId;
        usersService.deleteById(id);
        assertFalse(usersService.existsById(id));
    }
    @Test
    public void test_create_throws_NPE(){
        UserCreateDto dto = null;
        assertThrows(NullPointerException.class, () -> usersService.create(dto));
    }
    @Test
    public void test_create_throws_EEE(){
        String email = "hasenovsultanbek@gmail.com", username = "fring01";
        var userDto = new UserCreateDto();
        userDto.setFirstName(" ");
        userDto.setLastName(" ");
        userDto.setEmail(email);
        userDto.setUserName(username);
        assertThrows(EntityExistsException.class, () -> usersService.create(userDto));
    }
    @Test
    public void test_create_returns_created_user(){
        UserCreateDto user = new UserCreateDto();
        user.setUserName("sullek75");
        user.setEmail("sullek75@gmail.com");
        user.setFirstName("Saule");
        user.setLastName("Koldybaeva");
        UserCreatedDto createdUser = usersService.create(user);
        assertTrue(createdUser.getId() != null && createdUser.getId().compareTo(new UUID(0, 0)) > 0);
    }
    @Test
    public void test_update_user_throws_IAE(){
        UserUpdateDto dto = new UserUpdateDto();
        assertThrows(IllegalArgumentException.class, () -> usersService.update(dto));
    }
}
