package com.example.livestock.repository;

import com.example.livestock.entity.User;
import com.example.livestock.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndActiveTrue(String email);
    Page<User> findByActiveTrue(Pageable pageable);
    boolean existsByEmail(String email);
    List<User> findByRoleAndActiveTrue(Role role);
}
