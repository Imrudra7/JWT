package com.jwt.JWTApp.Repository;

import com.jwt.JWTApp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByUserId(Long id);

    Optional<User> findByUserEmail(String userEmail);
}
