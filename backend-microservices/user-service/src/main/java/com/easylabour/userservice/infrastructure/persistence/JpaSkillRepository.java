package com.easylabour.userservice.infrastructure.persistence;

import com.easylabour.userservice.domain.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
}
