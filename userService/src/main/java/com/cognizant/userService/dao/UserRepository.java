package com.cognizant.userService.dao;


import com.cognizant.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmail(String username);

    @Query("SELECT u.id FROM User u")
    List<Long> findAllUserIds();
}