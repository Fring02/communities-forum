package com.cloud.usersservice.integration.controller;

import com.cloud.usersservice.controller.UsersController;
import com.cloud.usersservice.dto.user.*;
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
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(SpringExtension.class)
@WebMvcTest(UsersController.class)
@ActiveProfiles("test")
public class UsersControllerTests {
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
                        "30c2249a-ea07-4ccb-9cd7-d783f9775daa"), 1, 2));
    }
    @ParameterizedTest
    @MethodSource("testParamsWithRequestParamsAndExistingUsers")
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetAllUsers_PresentParams_ExistingUsers_returnStatus200(int usersCount, List<String> testUUID,
                                                                            int page, int pageCount) throws Exception {
        List<UserViewDto> expectedUsers = new ArrayList<>(usersCount);
        for(String id : testUUID){
            expectedUsers.add(new UserViewDto() {
                @Override public String getEmail() {return "email";}
                @Override public String getUserName() {return "userName";}
                @Override public int getKarma() {return 1;}
                @Override public UUID getId() {return UUID.fromString(id);}
            });
        }
        expectedUsers = expectedUsers.stream().skip((long) page * pageCount).limit(pageCount).toList();
        var pageImpl = new PageImpl<>(expectedUsers, PageRequest.of(page - 1, pageCount), page);
        when(usersService.getAll(page, pageCount)).thenReturn(pageImpl);

        var expectedResult = objectMapper.writeValueAsString(new UsersPageDto(pageImpl.toList(), pageImpl.getTotalElements(),
                        pageImpl.getTotalPages())).replaceAll("\"", "");

        MvcResult result = mvc.perform(get("/api/v1/users").param("page", String.valueOf(page))
                        .param("pageCount", String.valueOf(pageCount)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count", is(expectedUsers.size())))
                .andExpect(jsonPath("$.pagesCount", is(page))).andReturn();

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
                },new UserViewDto() {
                    @Override public String getEmail() {return null;}
                    @Override public String getUserName() {return null;}
                    @Override public int getKarma() {return 0;}
                    @Override public UUID getId() {return UUID.randomUUID();}
                },new UserViewDto() {
                    @Override public String getEmail() {return null;}
                    @Override public String getUserName() {return null;}
                    @Override public int getKarma() {return 0;}
                    @Override public UUID getId() {return UUID.randomUUID();}
                });
        when(usersService.getByIds(any(Collection.class))).thenAnswer(i ->
                expectedFilteredUsers.stream().filter(u -> u.getId() == UUID.fromString(id)).toList());

        var expectedResult = objectMapper.writeValueAsString(new UsersListDto(expectedFilteredUsers, 1)).replaceAll("\"", "");
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
        when(usersService.getByIds(any(Collection.class))).thenAnswer(i -> expectedFilteredUsers);

        var expectedResult = objectMapper.writeValueAsString(new UsersListDto(expectedFilteredUsers, 2))
                .replaceAll("\"", "");
        var actualResult = mvc.perform(get("/api/v1/users/ids=" + id + "," + secondId)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(2))).andReturn();
        String actualUsers = actualResult.getResponse().getContentAsString().replaceAll("\"", "");
        assertEquals(expectedResult, actualUsers);
    }
    @ParameterizedTest
    @ValueSource(strings = {"be836563-ff6c-416c-adce-2cba29e443f8"})
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenExistsById_NoId_returnFalse(String id) throws Exception {
        when(usersService.existsById(any(UUID.class))).thenReturn(false);

        mvc.perform(get(String.format("/api/v1/users/%s/exists", id)))
                .andExpect(status().isOk())
                .andExpect(r -> r.getResponse().getContentAsString().equals(Boolean.toString(false))).andReturn();
    }
    @ParameterizedTest
    @ValueSource(strings = {"be836563-ff6c-416c-adce-2cba29e443f8"})
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenExistsById_anyId_returnTrue(String id) throws Exception {
        when(usersService.existsById(any(UUID.class))).thenReturn(true);
        mvc.perform(get(String.format("/api/v1/users/%s/exists", id)))
                .andExpect(status().isOk())
                .andExpect(r -> r.getResponse().getContentAsString().equals(Boolean.toString(true))).andReturn();
    }
    @Test
    public void whenGetById_unauthorized_returnStatus401() throws Exception {
        mvc.perform(get(String.format("/api/v1/users/ ")))
                .andExpect(status().isUnauthorized()).andReturn();
                //.andExpect(r -> r.getResponse().getContentAsString().equals("Id is invalid")).andReturn();
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetById_invalidId_returnStatus400() throws Exception {
        mvc.perform(get("/api/v1/users/1")).andExpect(status().isBadRequest()).andDo(print())
                .andExpect(r -> r.getResponse().getContentAsString().equals("Id is invalid")).andReturn();
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetById_user_not_exists_returnStatus404() throws Exception {
        mvc.perform(get("/api/v1/users/be836563-ff6c-416c-adce-2cba29e443f8")).andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetById_user_exists_returnStatus200() throws Exception {
        Optional<UserFullViewDto> user = Optional.of(new UserFullViewDto() {
            @Override
            public String getFirstName() {return "Sultan";}
            @Override
            public String getLastName() {return "Khassenov";}
            @Override
            public Collection<UserViewDto> getSubscribers() {return List.of();}
            @Override
            public Collection<UserViewDto> getSubscriberOf() {return List.of();}
            @Override
            public String getEmail() {return "hasenovsultanbek@gmail.com";}
            @Override
            public String getUserName() {return "fring0213";}
            @Override
            public int getKarma() {return 1;}
            @Override
            public UUID getId() {return UUID.fromString("be836563-ff6c-416c-adce-2cba29e443f8");}
        });
        when(usersService.getById(any(UUID.class))).thenReturn(user);
        var expectedResult = objectMapper.writeValueAsString(user.get()).replaceAll("\"", "");
        mvc.perform(get("/api/v1/users/be836563-ff6c-416c-adce-2cba29e443f8")).andExpect(status().isOk())
        .andExpect(r -> r.getResponse().getContentAsString()
                .replaceAll("\"", "").equals(expectedResult)).andReturn();
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetKarmaByUserId_emptyId_returnsStatus400() throws Exception {
        mvc.perform(get("/api/v1/users/1/karma")).andExpect(status().isBadRequest())
                .andExpect(r -> r.getResponse().getContentAsString().equals("Id is not of UUID format")).andReturn();
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetKarmaByUserId_existingId_returnsStatus200() throws Exception {
        UUID id = UUID.fromString("be836563-ff6c-416c-adce-2cba29e443f8");
        when(usersService.getKarmaById(any(UUID.class))).thenReturn(1);
        mvc.perform(get(String.format("/api/v1/users/%s/karma", id))).andExpect(status().isOk())
                .andExpect(r -> r.getResponse().getContentAsString().equals("1")).andReturn();
    }
    @Test
    @WithMockUser(username = "${mockUser.username}", password = "${mockUser.password}", roles = "${mockUser.roles}")
    public void whenGetRolesByUsername_returnsStatus200() throws Exception {
        UUID id = UUID.fromString("be836563-ff6c-416c-adce-2cba29e443f8");
        when(usersService.getRolesByUsername(any(String.class))).thenReturn(Optional.of(new UserWithRolesDto() {
            @Override
            public UUID getId() {return id;}
            @Override
            public Collection<String> getRoles() {return List.of("user", "admin", "superadmin");}
        }));
        mvc.perform(get("/api/v1/users/username/fring0213/roles"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("user", "admin", "superadmin"))).andReturn();
    }
}
