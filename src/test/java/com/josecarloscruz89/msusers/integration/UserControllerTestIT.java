package com.josecarloscruz89.msusers.integration;

import com.josecarloscruz89.msusers.integration.core.IntegrationTest;
import com.josecarloscruz89.msusers.model.entity.UserEntity;
import com.josecarloscruz89.msusers.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static com.josecarloscruz89.msusers.integration.factory.UserFactory.createUser;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class UserControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String USERS_ENDPOINT = "/users";

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
}