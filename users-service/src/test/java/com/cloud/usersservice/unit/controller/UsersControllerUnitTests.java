package com.cloud.usersservice.unit.controller;

import com.cloud.usersservice.controller.UsersController;
import com.cloud.usersservice.dto.user.UserViewDto;
import com.cloud.usersservice.dto.user.UsersListDto;
import com.cloud.usersservice.dto.user.UsersPageDto;
import com.cloud.usersservice.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(SpringExtension.class)
@WebMvcTest(UsersController.class)
@ActiveProfiles("test")
public class UsersControllerUnitTests {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UsersService usersService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetAllUsers_NoParams_NoUsers_returnStatus200() throws Exception {
        when(usersService.getAll()).thenReturn(Collections.emptyList());

        mvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.users", is(Collections.emptyList())));
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetAllUsers_InvalidPageParams_NoUsers_returnStatus200() throws Exception {
        when(usersService.getAll()).thenReturn(Collections.emptyList());

        mvc.perform(get("/api/v1/users").param("page", "-1").param("pageCount", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    static Stream<Arguments> testParamsWithNoRequestParamsAndExistingUsers(){
        return Stream.of(Arguments.of(1, "be836563-ff6c-416c-adce-2cba29e443f8"));
    }
    @ParameterizedTest
    @MethodSource("testParamsWithNoRequestParamsAndExistingUsers")
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetAllUsers_NoParams_ExistingUsers_returnStatus200(int usersCount, String testUUID) throws Exception {
        Collection<UserViewDto> expectedUsers = Collections.nCopies(usersCount, new UserViewDto() {
            @Override public String getEmail() {return "email";}
            @Override public String getUserName() {return "userName";}
            @Override public int getKarma() {return 0;}
            @Override public UUID getId() {return UUID.fromString(testUUID);}
        });
        when(usersService.getAll()).thenReturn(expectedUsers);
        var expectedResult = objectMapper.writeValueAsString(new UsersListDto(expectedUsers, usersCount))
                .replaceAll("\"", "");

        MvcResult result = mvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count", is(usersCount))).andReturn();
        String actualUsers = result.getResponse().getContentAsString().replaceAll("\"", "");

        assertEquals(expectedResult, actualUsers);
    }
    static Stream<Arguments> testParamsWithRequestParamsAndExistingUsers(){
        return Stream.of(Arguments.of(5,
                List.of("be836563-ff6c-416c-adce-2cba29e443f8", "780dea23-ae36-4117-ae6f-7c200caaaed8",
                        "3c6daf5e-b8a7-489e-afaf-68588b281c07", "08b631de-90d6-4b2c-96b6-f9e3a07b5352",
                        "30c2249a-ea07-4ccb-9cd7-d783f9775daa"),
                "1", "2"));
    }
    @ParameterizedTest
    @MethodSource("testParamsWithRequestParamsAndExistingUsers")
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetAllUsers_PresentParams_ExistingUsers_returnStatus200(int usersCount, List<String> testUUID,
                                                                            String page, String pageCount) throws Exception {
        var expectedUsers = new ArrayList<UserViewDto>(usersCount);
        int pageInt = Integer.parseInt(page), pageCountInt = Integer.parseInt(pageCount);
        AtomicInteger i = new AtomicInteger(0);
        testUUID.forEach(id -> expectedUsers.add(new UserViewDto() {
        @Override public String getEmail() {return "email";}
        @Override public String getUserName() {return "userName";}
        @Override public int getKarma() {return i.getAndIncrement();}
        @Override public UUID getId() {return UUID.fromString(id);}
        }));
        var pageImpl = new PageImpl<>(expectedUsers, PageRequest.of(pageInt - 1, pageCountInt), usersCount);
        when(usersService.getAll(pageInt, pageCountInt)).thenReturn(pageImpl);

        var expectedResult = objectMapper.writeValueAsString(new UsersPageDto(pageImpl.toList(), pageImpl.getTotalElements(),
                        pageImpl.getTotalPages())).replaceAll("\"", "");

        MvcResult result = mvc.perform(get("/api/v1/users").param("page", page).param("pageCount", pageCount))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count", is(usersCount)))
                .andExpect(jsonPath("$.pagesCount", is(pageCountInt))).andReturn();

        String actualUsers = result.getResponse().getContentAsString().replaceAll("\"", "");

        assertEquals(expectedResult, actualUsers);
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetByIds_NoIds_returnStatus204() throws Exception {
        when(usersService.getByIds(Collections.emptyList())).thenReturn(Collections.emptyList());
        mvc.perform(get("/api/v1/users/ids=")).andDo(print()).andExpect(status().isNoContent());
    }
    @ParameterizedTest
    @ValueSource(strings = {"be836563-ff6c-416c-adce-2cba29e443f8"})
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetByIds_AnyIds_Match_returnStatus200(String id) throws Exception {
        List<UserViewDto> expectedFilteredUsers = List.of(new UserViewDto() {
                    @Override public String getEmail() {return null;}
                    @Override public String getUserName() {return null;}
                    @Override public int getKarma() {return 0;}
                    @Override public UUID getId() {return UUID.fromString(id);}
                });
        when(usersService.getByIds(List.of(UUID.fromString(id))))
                .thenReturn(expectedFilteredUsers);

        var expectedResult = objectMapper.writeValueAsString(new UsersListDto(expectedFilteredUsers, 1))
                .replaceAll("\"", "");
        var actualResult = mvc.perform(get("/api/v1/users/ids=" + id)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1))).andReturn();
        String actualUsers = actualResult.getResponse().getContentAsString().replaceAll("\"", "");
        assertEquals(expectedResult, actualUsers);
    }
    static Stream<Arguments> testIds(){
        return Stream.of(Arguments.of("be836563-ff6c-416c-adce-2cba29e443f8", "780dea23-ae36-4117-ae6f-7c200caaaed8"));
    }
    @ParameterizedTest
    @MethodSource("testIds")
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetByIds_AllIds_Match_returnStatus200(String id, String secondId) throws Exception {
        List<UserViewDto> expectedFilteredUsers = List.of(new UserViewDto() {
            @Override public String getEmail() {return null;}
            @Override public String getUserName() {return null;}
            @Override public int getKarma() {return 0;}
            @Override public UUID getId() {return UUID.fromString(id);}
        }, new UserViewDto() {
            @Override public String getEmail() {return null;}
            @Override public String getUserName() {return null;}
            @Override public int getKarma() {return 0;}
            @Override public UUID getId() {return UUID.fromString(secondId);}
        });
        when(usersService.getByIds(List.of(UUID.fromString(id), UUID.fromString(secondId)))).thenReturn(expectedFilteredUsers);

        var expectedResult = objectMapper.writeValueAsString(new UsersListDto(expectedFilteredUsers, 2))
                .replaceAll("\"", "");
        var actualResult = mvc.perform(get("/api/v1/users/ids=" + id)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(2))).andReturn();
        String actualUsers = actualResult.getResponse().getContentAsString().replaceAll("\"", "");
        assertEquals(expectedResult, actualUsers);
    }
    @ParameterizedTest
    @ValueSource(strings = {"be836563-ff6c-416c-adce-2cba29e443f8"})
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenExistsById_NoId_returnFalse(String id) throws Exception {
        when(usersService.existsById(UUID.fromString(id))).thenReturn(false);

        mvc.perform(get("/api/v1/users/ids=" + id)).andExpect(status().isOk())
                .andExpect()
                .andExpect(r -> r.getResponse().getContentAsString().equals(Boolean.toString(false))).andReturn();
    }
}
