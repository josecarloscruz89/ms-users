package com.josecarloscruz89.msusers.integration;

import com.josecarloscruz89.msusers.integration.core.IntegrationTest;
import com.josecarloscruz89.msusers.model.entity.UserEntity;
import com.josecarloscruz89.msusers.repository.UserRepository;
import com.josecarloscruz89.msusers.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.josecarloscruz89.msusers.integration.factory.UserFactory.createUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
public class UserControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_BY_ID_ENDPOINT = "/users/{userId}";
    private static final Integer UUID_LENGTH = UUID.randomUUID().toString().length();

    @BeforeEach
    public void setup() {
        deleteFromTables(jdbcTemplate, "users");
    }


    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() throws Exception {
        UserEntity userTestOne = createUser("userTestOne", 30);
        UserEntity userTestTwo = createUser("userTestTwo", 40);

        userRepository.save(userTestOne);
        userRepository.save(userTestTwo);

        mockMvc.perform(get(USERS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("userTestOne", "userTestTwo")))
                .andExpect(jsonPath("$[*].age", containsInAnyOrder(30, 40)));
    }

    @Test
    @DisplayName("Should return an user by id")
    void shouldReturnAnUserById() throws Exception {
        UserEntity userTestGetUserById = createUser("userTestGetUserById", 15);

        userRepository.save(userTestGetUserById);

        String uuid = userTestGetUserById.getUuid();

        mockMvc.perform(get(USERS_BY_ID_ENDPOINT, uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("userTestGetUserById")))
                .andExpect(jsonPath("$.age", is(15)))
                .andExpect(jsonPath("$.uuid", is(uuid)))
                .andExpect(jsonPath("$.uuid", hasLength(uuid.length())));
    }

    @Test
    @DisplayName("Should return 404 NotFound Exception when the user does not exist")
    void shouldReturn404NotFoundExceptionWhenTheUserDoesNotExist() throws Exception {
        String uuid = "999";

        mockMvc.perform(get(USERS_BY_ID_ENDPOINT, uuid))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create a new user")
    void shouldCreateANewUser() throws Exception {
        String requestBody = FileUtils.getJSONFromFile("createUser.json");

        mockMvc.perform(post(USERS_ENDPOINT)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", hasLength(UUID_LENGTH)));
    }

    @Test
    @DisplayName("Should return 400 when request body does not exist")
    void shouldReturn400WhenRequestBodyDoesNOtExist() throws Exception {
        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update an existing user and return no content")
    void shouldUpdateAnExistingUserAndReturnNoContent() throws Exception {
        UserEntity userPutTest = createUser("userPutTest", 30);
        userRepository.save(userPutTest);
        String uuid = userPutTest.getUuid();

        String requestBody = FileUtils.getJSONFromFile("updateUser.json");

        mockMvc.perform(put(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        UserEntity updatedUser = userRepository.findById(uuid).get();
        assertThat(updatedUser.getUuid(), is(uuid));
        assertThat(updatedUser.getName(), is("Updated User Integration Test"));
        assertThat(updatedUser.getAge(), is(70));
    }

    @Test
    @DisplayName("Should return bad request when request body is empty")
    void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
        UserEntity userPutTest = createUser("userPutTest", 30);
        userRepository.save(userPutTest);
        String uuid = userPutTest.getUuid();

        mockMvc.perform(put(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return not found when the userId is invalid")
    void shouldReturnNotFoundWhenTheUserIdIsInvalid() throws Exception {
        String uuid = "123456";

        String requestBody = FileUtils.getJSONFromFile("updateUser.json");

        mockMvc.perform(put(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return bad request when age key is not present")
    void shouldReturnBadRequestWhenAgeKeyIsNotPresent() throws Exception {
        UserEntity userPutTest = createUser("userPutTest", 30);
        userRepository.save(userPutTest);
        String uuid = userPutTest.getUuid();

        String requestBody = FileUtils.getJSONFromFile("updateUserWithoutAge.json");

        mockMvc.perform(put(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when name key is not present")
    void shouldReturnBadRequestWhenNameKeyIsNotPresent() throws Exception {
        UserEntity userPutTest = createUser("userPutTest", 30);
        userRepository.save(userPutTest);
        String uuid = userPutTest.getUuid();

        String requestBody = FileUtils.getJSONFromFile("updateUserWithoutName.json");

        mockMvc.perform(put(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when name key is not a string")
    void shouldReturnBadRequestWhenNameKeyIsNotAString() throws Exception {
        UserEntity userPutTest = createUser("userPutTest", 30);
        userRepository.save(userPutTest);
        String uuid = userPutTest.getUuid();

        String requestBody = FileUtils.getJSONFromFile("updateUserNameIsNotAString.json");

        mockMvc.perform(put(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Should return bad request when the age is not an Integer")
    void shouldReturnBadRequestWhenTheAgeIsNotAnInteger() throws Exception {
        UserEntity userPutTest = createUser("userPutTest", 30);
        userRepository.save(userPutTest);
        String uuid = userPutTest.getUuid();

        String requestBody = FileUtils.getJSONFromFile("updateUserAgeIsNotAnInteger.json");

        mockMvc.perform(put(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should partially update an existing user name")
    void shouldPartiallyUpdateAnExistingUserName() throws Exception {
        UserEntity userPatchNameTest = createUser("userPatchNameTest", 40);
        userRepository.save(userPatchNameTest);
        String uuid = userPatchNameTest.getUuid();

        String requestBody = FileUtils.getJSONFromFile("partiallyUpdateUserName.json");

        mockMvc.perform(patch(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        UserEntity updatedUser = userRepository.findById(uuid).get();
        assertThat(updatedUser.getUuid(), is(uuid));
        assertThat(updatedUser.getName(), is("Partially Updated User"));
        assertThat(updatedUser.getAge(), is(40));
    }

    @Test
    @DisplayName("Should partially update an existing user age")
    void shouldPartiallyUpdateAnExistingUserAge() throws Exception {
        UserEntity userPatchAgeTest = createUser("userPatchAgeTest", 40);
        userRepository.save(userPatchAgeTest);
        String uuid = userPatchAgeTest.getUuid();

        String requestBody = FileUtils.getJSONFromFile("partiallyUpdateUserAge.json");

        mockMvc.perform(patch(USERS_BY_ID_ENDPOINT, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        UserEntity updatedUser = userRepository.findById(uuid).get();
        assertThat(updatedUser.getUuid(), is(uuid));
        assertThat(updatedUser.getName(), is("userPatchAgeTest"));
        assertThat(updatedUser.getAge(), is(55));
    }

    @Test
    @DisplayName("Should delete an existing user and return no content")
    void shouldDeleteAnExistingUserAndReturnNoContent() throws Exception {
        UserEntity userDeleteTest = createUser("userDeleteTest", 50);
        userRepository.save(userDeleteTest);
        String uuid = userDeleteTest.getUuid();

        mockMvc.perform(delete(USERS_BY_ID_ENDPOINT, uuid))
                .andExpect(status().isNoContent());

    }
}