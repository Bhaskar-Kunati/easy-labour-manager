package com.easylabour.userservice.domain.repository;

import com.easylabour.userservice.domain.model.WorkerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerSkillRepository extends JpaRepository<WorkerSkill, Long> {
    List<WorkerSkill> findByWorkerId(Long workerId);
}
