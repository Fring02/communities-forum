package com.cloud.usersservice.unit.entity;

import com.cloud.usersservice.entity.User;
import com.cloud.usersservice.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserModelTests {
    @Mock
    private UsersRepository repository;
    private User user;
    private User userCopy;
    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserName("fring01");
        user.setFirstName("Sultan");
        user.setLastName("Khassenov");
        user.setEmail("hasenovsultanbek@gmail.com");
    }
    @Test
    public void should_return_true_for_user_equals_if_users_are_the_same(){
        //Arrange
        userCopy = new User();
        userCopy.setUserName("fring01");
        userCopy.setFirstName("Sultan");
        userCopy.setLastName("Khassenov");
        userCopy.setEmail("hasenovsultanbek@gmail.com");
        when(repository.save(any(User.class))).thenReturn(userCopy);
        user = repository.save(user);
        assertEquals(user, userCopy);
    }

    @Test
    public void should_return_false_for_user_equals_if_users_are_the_same(){
        //Arrange
        userCopy = new User();
        userCopy.setUserName("frik02");
        userCopy.setFirstName("Sultan");
        userCopy.setLastName("Khassenov");
        userCopy.setEmail("s.khassen@gmu.edu");
        when(repository.save(any(User.class))).thenReturn(user);
        user = repository.save(user);
        assertNotEquals(user, userCopy);
    }
    @Test
    public void should_return_false_for_user_equals_if_users_type_mismatch(){
        //Arrange
        var stubObject = new Object();
        assertNotEquals(user, stubObject);
        stubObject = null;
        assertNotEquals(user, stubObject);
    }
}
