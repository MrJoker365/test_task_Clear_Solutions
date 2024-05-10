package com.example.test_task_users.repo;

import com.example.test_task_users.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {

    List<Users> findAllByBirthDateBetween(Date fromDate, Date toDate);

    Optional<Users> findAllByEmail(String email);


}
