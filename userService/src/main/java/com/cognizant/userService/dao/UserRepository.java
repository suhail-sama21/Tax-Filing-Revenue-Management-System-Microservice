package com.cognizant.userService.dao;

import com.cognizant.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);

    // ADD THIS LINE
    Optional<User> findByEmail(String email);
}