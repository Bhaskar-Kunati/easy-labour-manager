package com.easylabour.catalogservice.application.service;

import com.easylabour.catalogservice.domain.model.Service;
import com.easylabour.catalogservice.domain.model.ServiceStatus;
import com.easylabour.catalogservice.domain.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<Service> getActiveServices(Long categoryId) {
        if (categoryId != null) {
            return serviceRepository.findByCategoryIdAndStatus(categoryId, ServiceStatus.ACTIVE);
        }
        return serviceRepository.findByStatus(ServiceStatus.ACTIVE);
    }
    
    @Transactional(readOnly = true)
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id).orElseThrow();
    }
    
    @Transactional(readOnly = true)
    public List<Service> searchServices(String query) {
        return serviceRepository.searchActiveServices(query);
    }
    
    @Transactional
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }
    
    @Transactional
    public Service updateService(Long id, Service serviceUpdate) {
        Service service = serviceRepository.findById(id).orElseThrow();
        service.setName(serviceUpdate.getName());
        service.setDescription(serviceUpdate.getDescription());
        service.setPricingType(serviceUpdate.getPricingType());
        service.setBasePrice(serviceUpdate.getBasePrice());
        service.setStatus(serviceUpdate.getStatus());
        service.setEstimatedDuration(serviceUpdate.getEstimatedDuration());
        service.setImage(serviceUpdate.getImage());
        return serviceRepository.save(service);
    }
}
