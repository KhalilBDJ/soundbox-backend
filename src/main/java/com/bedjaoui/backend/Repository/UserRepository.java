package com.bedjaoui.backend.Repository;

import com.bedjaoui.backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
