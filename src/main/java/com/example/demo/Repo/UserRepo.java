package com.example.demo.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;

import feign.Param;
@RepositoryRestController
public interface UserRepo extends JpaRepository<User,Long> {
     Optional<User> findByEmail(String email);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findUsersByRole(@Param("role") Role role);
}
