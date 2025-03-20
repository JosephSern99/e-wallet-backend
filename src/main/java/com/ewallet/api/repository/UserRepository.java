package com.ewallet.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ewallet.api.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
