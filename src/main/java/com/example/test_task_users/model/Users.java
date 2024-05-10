package com.example.test_task_users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Email field cannot be empty.")
    @Email(message = "Invalid email format.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format."
    )
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "First_Name field cannot be empty.")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last_Name field cannot be empty.")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Birth Date is required.")
    @Past(message = "Birth Date must be a past date.")
    @Column(nullable = false)
    private Date birthDate;

    private String address;

    private String phoneNumber;

}
