package com.easylabour.userservice.infrastructure.persistence;

import com.easylabour.userservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaUserRepository extends JpaRepository<User, String> {
    List<User> findBySkills_Name(String skillName);
    java.util.Optional<User> findByEmail(String email);
    java.util.Optional<User> findByPhoneNumber(String phoneNumber);
}
