package com.easylabour.catalogservice.application.service;

import com.easylabour.catalogservice.domain.model.ServiceCategory;
import com.easylabour.catalogservice.domain.model.ServiceStatus;
import com.easylabour.catalogservice.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ServiceCategory> getAllActiveCategories() {
        return categoryRepository.findByStatusOrderByDisplayOrderAsc(ServiceStatus.ACTIVE);
    }
    
    @Transactional(readOnly = true)
    public ServiceCategory getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    @Transactional
    public ServiceCategory createCategory(ServiceCategory category) {
        return categoryRepository.save(category);
    }
    
    @Transactional
    public ServiceCategory updateCategory(Long id, ServiceCategory categoryUpdate) {
        ServiceCategory category = categoryRepository.findById(id).orElseThrow();
        category.setName(categoryUpdate.getName());
        category.setDescription(categoryUpdate.getDescription());
        category.setIcon(categoryUpdate.getIcon());
        category.setImage(categoryUpdate.getImage());
        category.setStatus(categoryUpdate.getStatus());
        category.setDisplayOrder(categoryUpdate.getDisplayOrder());
        return categoryRepository.save(category);
    }
}
