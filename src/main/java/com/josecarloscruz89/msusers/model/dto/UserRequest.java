package com.josecarloscruz89.msusers.model.dto;

import com.josecarloscruz89.msusers.validation.annotation.ValidName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotEmpty(message = "The field name is required")
    @ValidName
    private String name;

    @NotNull(message = "The field age is required")
    private Integer age;

}