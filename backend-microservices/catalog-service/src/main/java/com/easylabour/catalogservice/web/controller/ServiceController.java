package com.easylabour.catalogservice.web.controller;

import com.easylabour.catalogservice.application.service.ServiceService;
import com.easylabour.catalogservice.domain.model.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;

    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices(@RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(serviceService.getActiveServices(categoryId));
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @GetMapping("/services/search")
    public ResponseEntity<List<Service>> searchServices(@RequestParam String query) {
        return ResponseEntity.ok(serviceService.searchServices(query));
    }

    @PostMapping("/admin/services")
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        return ResponseEntity.ok(serviceService.createService(service));
    }

    @PutMapping("/admin/services/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service service) {
        return ResponseEntity.ok(serviceService.updateService(id, service));
    }
}
