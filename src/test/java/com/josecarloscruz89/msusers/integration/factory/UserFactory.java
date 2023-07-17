package com.josecarloscruz89.msusers.integration.factory;

import com.josecarloscruz89.msusers.model.entity.UserEntity;

public class UserFactory {
    public static UserEntity createUser(String name, int age) {
        return UserEntity.builder()
                .name(name)
                .age(age)
                .build();
    }
}