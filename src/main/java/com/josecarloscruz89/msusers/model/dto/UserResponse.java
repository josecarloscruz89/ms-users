package com.josecarloscruz89.msusers.model.dto;

import com.josecarloscruz89.msusers.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String uuid;

    private String name;

    private Integer age;

    public static UserResponse valueOf(UserEntity userEntity) {
        return UserResponse.builder()
                .age(userEntity.getAge())
                .name(userEntity.getName())
                .uuid(userEntity.getUuid())
                .build();
    }

}