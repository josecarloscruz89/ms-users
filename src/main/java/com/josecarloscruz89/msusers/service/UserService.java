package com.josecarloscruz89.msusers.service;

import com.josecarloscruz89.msusers.model.dto.UserResponse;
import com.josecarloscruz89.msusers.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
}