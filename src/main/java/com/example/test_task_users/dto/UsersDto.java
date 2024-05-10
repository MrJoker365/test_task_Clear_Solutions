package com.example.test_task_users.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class UsersDto {


    @NotBlank(message = "Email field cannot be empty.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format."
    )
    private String email;

    private String firstName;

    private String lastName;

    private Date birthDate;

    private String address;

    private String phoneNumber;

}
