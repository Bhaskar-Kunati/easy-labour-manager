package com.easylabour.userservice.application.service;

import com.easylabour.userservice.domain.model.Address;
import com.easylabour.userservice.domain.repository.AddressRepository;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final JpaUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Address> getAddresses(String userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public Address createAddress(String userId, Address address) {
        // Handle isDefault: if new address is default, reset others
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            resetDefaultAddresses(userId);
        }
        
        // Fetch the user properly
        address.setUser(userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found")));
            
        return addressRepository.save(address);
    }
    
    @Transactional
    public Address updateAddress(String userId, Long addressId, Address addressUpdate) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
            
        address.setAddressType(addressUpdate.getAddressType());
        address.setFullAddress(addressUpdate.getFullAddress());
        address.setLandmark(addressUpdate.getLandmark());
        address.setCity(addressUpdate.getCity());
        address.setState(addressUpdate.getState());
        address.setPostalCode(addressUpdate.getPostalCode());
        address.setLatitude(addressUpdate.getLatitude());
        address.setLongitude(addressUpdate.getLongitude());
        
        if (Boolean.TRUE.equals(addressUpdate.getIsDefault())) {
            resetDefaultAddresses(userId);
            address.setIsDefault(true);
        }
        
        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(String userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(address);
    }
    
    @Transactional
    public void setDefaultAddress(String userId, Long addressId) {
        resetDefaultAddresses(userId);
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private void resetDefaultAddresses(String userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address addr : addresses) {
            addr.setIsDefault(false);
        }
        addressRepository.saveAll(addresses);
    }
}
