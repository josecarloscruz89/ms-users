package com.josecarloscruz89.msusers.service;

import com.josecarloscruz89.msusers.exception.NotFoundException;
import com.josecarloscruz89.msusers.model.dto.UserRequest;
import com.josecarloscruz89.msusers.model.dto.UserResponse;
import com.josecarloscruz89.msusers.model.entity.UserEntity;
import com.josecarloscruz89.msusers.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        log.info("Getting all users...");
        return userRepository.findAll()
                .stream()
                .map(UserResponse::valueOf)
                .toList();
    }

    public UserResponse getUserById(String userId) {
        log.info("Getting user by id {}", userId);
        return userRepository.findById(userId)
                .map(UserResponse::valueOf)
                .orElseThrow(NotFoundException::new);
    }

    public String createUser(UserRequest userRequest) {
        log.info("Creating a new user with: {}", userRequest);

        UserEntity userEntity = UserEntity.valueOf(userRequest);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        UserEntity savedEntity = userRepository.save(userEntity);

        return savedEntity.getUuid();
    }

    public void updateUser(UserRequest userRequest, String userId) {
        log.info("Updating userId: {} with {}", userId, userRequest);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        userEntity.updateValues(userRequest);

        userRepository.save(userEntity);
    }

    public void partialUpdateUser(UserRequest userRequest, String userId) {
        log.info("Partial updating userId: {} with {}", userId, userRequest);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        Optional.ofNullable(userRequest.getName()).ifPresent(userEntity::setName);
        Optional.ofNullable(userRequest.getAge()).ifPresent(userEntity::setAge);

        userEntity.setUpdatedAt(LocalDateTime.now());

        userRepository.save(userEntity);
    }
}