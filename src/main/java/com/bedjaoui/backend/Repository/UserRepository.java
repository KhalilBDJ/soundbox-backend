package com.bedjaoui.backend.Repository;

import com.bedjaoui.backend.Model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
}
