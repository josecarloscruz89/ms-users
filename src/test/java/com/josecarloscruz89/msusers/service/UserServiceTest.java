package com.josecarloscruz89.msusers.service;

import com.josecarloscruz89.msusers.exception.NotFoundException;
import com.josecarloscruz89.msusers.model.dto.UserResponse;
import com.josecarloscruz89.msusers.model.entity.UserEntity;
import com.josecarloscruz89.msusers.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final List<UserEntity> entities = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        UserEntity userOne = UserEntity.builder()
                .age(20)
                .name("Bob")
                .uuid(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserEntity userTwo = UserEntity.builder()
                .age(30)
                .name("John")
                .uuid(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entities.add(userOne);
        entities.add(userTwo);
    }

    @Test
    @DisplayName("Should throw a NotFoundException due to userId does not exist")
    public void shouldThrowANotFoundExceptionWhenGetUserById() {
        String invalidId = "123";

        given(userRepository.findById(invalidId))
                .willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(invalidId));

        verify(userRepository, times(1)).findById(invalidId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should return an user by id")
    public void shouldReturnAnUserById() {
        UserEntity userEntity = entities.get(0);

        given(userRepository.findById(userEntity.getUuid()))
                .willReturn(Optional.of(userEntity));

        UserResponse userResponse = userService.getUserById(userEntity.getUuid());

        assertNotNull(userResponse);
        assertEquals(userResponse.getUuid(), userEntity.getUuid());
        assertEquals(userResponse.getName(), userEntity.getName());
        assertEquals(userResponse.getAge(), userEntity.getAge());

        verify(userRepository, times(1)).findById(userEntity.getUuid());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should return a list with all users")
    public void shouldReturnAListWithAllUsers() {
        //given
        given(userRepository.findAll())
                .willReturn(entities);

        List<UserResponse> entitiesConverted = entities.stream()
                .map(UserResponse::valueOf)
                .collect(Collectors.toList());

        //when
        List<UserResponse> userResponses = userService.getAllUsers();

        //then
        assertEquals(entitiesConverted, userResponses);

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should return an empty list")
    public void shouldReturnAnEmptyList() {
        given(userRepository.findAll())
                .willReturn(Collections.emptyList());

        List<UserResponse> userResponses = userService.getAllUsers();

        assertEquals(userResponses.size(), 0);

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }
}