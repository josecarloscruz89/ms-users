package com.josecarloscruz89.msusers.controller;

import com.josecarloscruz89.msusers.exception.NotFoundException;
import com.josecarloscruz89.msusers.model.dto.UserRequest;
import com.josecarloscruz89.msusers.model.dto.UserResponse;
import com.josecarloscruz89.msusers.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(SpringExtension.class)
@DisplayName("User Controller Tests")
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USERS_ENDPOINT = "/users";
    private static final String USER_BY_ID_ENDPOINT = "/users/{userId}";

    @Test
    @DisplayName("Should delete an user by id")
    public void shouldDeleteAnUserById() throws Exception {
        String uuid = UUID.randomUUID().toString();

        mockMvc.perform(delete(USER_BY_ID_ENDPOINT, uuid))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(uuid);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should partial update an user")
    public void shouldPartialUpdateAnUser() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("Jose")
                .build();

        String uuid = UUID.randomUUID().toString();

        byte[] body = objectMapper.writeValueAsBytes(userRequest);

        mockMvc.perform(patch(USER_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).partialUpdateUser(userRequest, uuid);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should create a new user")
    public void shouldCreateANewUser() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("Jose")
                .age(33)
                .build();

        String createdUuid = UUID.randomUUID().toString();

        when(userService.createUser(userRequest))
                .thenReturn(createdUuid);

        byte[] body = objectMapper.writeValueAsBytes(userRequest);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", createdUuid));

        verify(userService, times(1)).createUser(userRequest);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should not update an user by id due to field name has numbers")
    public void shouldNotUpdateUserByIdDueToFieldNameHasNumbers() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("Jose123")
                .age(33)
                .build();

        String uuid = UUID.randomUUID().toString();

        byte[] body = objectMapper.writeValueAsBytes(userRequest);

        mockMvc.perform(put(USER_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is("The field name shouldn't accept numbers")));

        verify(userService, times(0)).updateUser(userRequest, uuid);
    }

    @Test
    @DisplayName("Should not update an user by id due to missing fields")
    public void shouldNotUpdateUserDueToMissingFields() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .build();

        String uuid = UUID.randomUUID().toString();

        byte[] body = objectMapper.writeValueAsBytes(userRequest);

        mockMvc.perform(put(USER_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].message", hasItem("The field name is required")))
                .andExpect(jsonPath("$[*].message", hasItem("The field age is required")));

        verify(userService, times(0)).updateUser(userRequest, uuid);
    }

    @Test
    @DisplayName("Should not update an user by id due to missing name field")
    public void shouldNotUpdateUserDueToMissingNameField() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .age(33)
                .build();

        String uuid = UUID.randomUUID().toString();

        byte[] body = objectMapper.writeValueAsBytes(userRequest);

        mockMvc.perform(put(USER_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is("The field name is required")));

        verify(userService, times(0)).updateUser(userRequest, uuid);
    }

    @Test
    @DisplayName("Should update a user by id")
    public void shouldUpdateAUserById() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("Jose")
                .age(33)
                .build();

        String uuid = UUID.randomUUID().toString();

        byte[] body = objectMapper.writeValueAsBytes(userRequest);

        mockMvc.perform(put(USER_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updateUser(userRequest, uuid);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should return 404 NotFound when the id does not exist")
    public void shouldReturnNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        String invalidId = "abc123";

        when(userService.getUserById(invalidId))
                .thenThrow(new NotFoundException());

        mockMvc.perform(get(USER_BY_ID_ENDPOINT, invalidId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(invalidId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should return an user by id")
    public void shouldReturnAnUserById() throws Exception {
        String uuid = UUID.randomUUID().toString();

        UserResponse userResponse = UserResponse.builder()
                .name("Jose")
                .age(33)
                .uuid(uuid)
                .build();

        when(userService.getUserById(uuid))
                .thenReturn(userResponse);

        mockMvc.perform(get(USER_BY_ID_ENDPOINT, uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(uuid)))
                .andExpect(jsonPath("$.name", is("Jose")))
                .andExpect(jsonPath("$.age", is(33)));

        verify(userService, times(1)).getUserById(uuid);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should return a list with all users")
    public void shouldReturnAListWithAllUsers() throws Exception {
        List<UserResponse> users = new ArrayList<>();

        String uuid = UUID.randomUUID().toString();

        UserResponse userResponse = UserResponse.builder()
                .name("Jose")
                .age(33)
                .uuid(uuid)
                .build();

        users.add(userResponse);

        when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform(get(USERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid", is(uuid)))
                .andExpect(jsonPath("$[0].name", is("Jose")))
                .andExpect(jsonPath("$[0].age", is(33)));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

}