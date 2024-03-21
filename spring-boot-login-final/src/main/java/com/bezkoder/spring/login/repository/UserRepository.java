package com.bezkoder.spring.login.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bezkoder.spring.login.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
 Optional<User> findByUsername(String username);

 Boolean existsByUsername(String username);

 List<User> findByRoleAndIdNot(String role, UUID userId);

}
