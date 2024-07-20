package com.cloud.usersservice.unit.service;

import com.cloud.usersservice.dto.user.UserFullViewDto;
import com.cloud.usersservice.dto.user.UserViewDto;
import com.cloud.usersservice.repository.UsersRepository;
import com.cloud.usersservice.service.UsersService;
import com.cloud.usersservice.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.TestPropertySource;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application-test.properties")
public class UsersServiceTests {
    @Autowired
    private UsersService usersService;
    @MockBean
    private UsersRepository repository;
    @MockBean
    private ModelMapper mapper;
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @Mock
    private CacheManager cacheManager;
    //Logger mocking
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        usersService = new UsersServiceImpl(repository, mapper, rabbitTemplate, cacheManager);
    }
    @AfterEach
    public void tearDown() {
        System.setOut(System.out);
        System.setErr(System.err);
    }
    @Test
    public void should_return_getAll_empty_users(){
        var users = usersService.getAll();
        assertTrue(outContent.toString().contains("Fetching all users..."));
        verify(repository).findBy(UserViewDto.class);
        assertTrue(users.isEmpty());
    }
    @Test
    public void should_return_getAll_any_users(){
        //Arrange
        when(usersService.getAll()).thenReturn(List.of(new UserViewDto() {
            @Override
            public String getEmail() {return "hasenovsultanbek@gmail.com";}
            @Override
            public String getUserName() {return "username";}
            @Override
            public int getKarma() {return 1;}
            @Override
            public UUID getId() {return null;}
        }));
        var users = usersService.getAll();
        assertTrue(outContent.toString().contains("Fetching all users..."));
        verify(repository).findBy(UserViewDto.class);
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(u -> "hasenovsultanbek@gmail.com".equals(u.getEmail())));
    }
    @Test
    public void should_throw_IAE_when_getAll_users_by_zero_page(){
        int pageCount = 0, page = 0;
        assertThrows(IllegalArgumentException.class, () -> usersService.getAll(page, pageCount));
    }
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void should_return_page_getAll_users_by_fixed_page(int page){
        //Arrange
        int pageCount = 10;
        var testUser = mock(UserViewDto.class);
        var testUsers = new ArrayList<UserViewDto>(20);
        IntStream.range(0, 20).forEach(i -> testUsers.add(testUser));
        Pageable pageable = PageRequest.of(page - 1, pageCount).withSort(Sort.by("email").ascending());
        when(repository.findBy(UserViewDto.class, pageable)).thenReturn(new PageImpl<>(testUsers, pageable, testUsers.size()));
        //act
        var response = usersService.getAll(page, pageCount);
        //assert
        assertTrue(outContent.toString().contains(String.format("Fetching %d users by page %d...", pageCount, page - 1)));
        assertTrue(response.hasContent());
        assertEquals(response.getTotalElements(), testUsers.size());
        assertEquals(page - 1, response.getPageable().getPageNumber());
    }
}
