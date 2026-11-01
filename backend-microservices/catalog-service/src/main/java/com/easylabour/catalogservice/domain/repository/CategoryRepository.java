package com.easylabour.catalogservice.domain.repository;

import com.easylabour.catalogservice.domain.model.ServiceCategory;
import com.easylabour.catalogservice.domain.model.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ServiceCategory, Long> {
    List<ServiceCategory> findByStatusOrderByDisplayOrderAsc(ServiceStatus status);
}
