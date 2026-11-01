package com.easylabour.catalogservice.domain.repository;

import com.easylabour.catalogservice.domain.model.Service;
import com.easylabour.catalogservice.domain.model.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByStatus(ServiceStatus status);
    List<Service> findByCategoryIdAndStatus(Long categoryId, ServiceStatus status);
    
    @Query("SELECT s FROM Service s WHERE s.status = 'ACTIVE' AND (s.name LIKE %:query% OR s.description LIKE %:query% OR s.category.name LIKE %:query%)")
    List<Service> searchActiveServices(String query);
}
