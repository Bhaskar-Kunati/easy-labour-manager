package com.easylabour.userservice.application.service;

import com.easylabour.common.dto.UserDto;
import com.easylabour.common.exception.ResourceNotFoundException;
import com.easylabour.userservice.domain.model.Skill;
import com.easylabour.userservice.domain.model.User;
import com.easylabour.userservice.domain.model.UserType;
import com.easylabour.userservice.infrastructure.persistence.JpaSkillRepository;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository userRepository;
    private final JpaSkillRepository skillRepository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userDto.getUserType() == null) {
            throw new IllegalArgumentException("User type is required");
        }
        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .phoneNumber(userDto.getPhoneNumber())
                .userType(UserType.valueOf(userDto.getUserType()))
                .averageRating(userDto.getAverageRating())
                .reviewCount(userDto.getReviewCount())
                .completedBookingsCount(userDto.getCompletedBookingsCount())
                .skills(mapSkills(userDto.getSkills()))
                .build();
        
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }

    public List<UserDto> searchBySkill(String skillName) {
        return userRepository.findBySkills_Name(skillName).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateSkills(String id, Set<String> skillNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setSkills(mapSkills(skillNames));
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Transactional
    public void updateUserRating(String userId, Integer newRating) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        int currentCount = user.getReviewCount() != null ? user.getReviewCount() : 0;
        double currentRating = user.getAverageRating() != null ? user.getAverageRating() : 0.0;
        
        double newAverage = ((currentRating * currentCount) + newRating) / (currentCount + 1);
        
        user.setReviewCount(currentCount + 1);
        user.setAverageRating(newAverage);
        
        userRepository.save(user);
    }

    @Transactional
    public void incrementCompletedBookings(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        int currentCount = user.getCompletedBookingsCount() != null ? user.getCompletedBookingsCount() : 0;
        user.setCompletedBookingsCount(currentCount + 1);
        
        userRepository.save(user);
    }

    private Set<Skill> mapSkills(Set<String> skillNames) {
        if (skillNames == null) return Set.of();
        return skillNames.stream()
                .map(name -> skillRepository.findByName(name)
                        .orElseGet(() -> skillRepository.save(Skill.builder().name(name).build())))
                .collect(Collectors.toSet());
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType().name())
                .averageRating(user.getAverageRating())
                .reviewCount(user.getReviewCount())
                .completedBookingsCount(user.getCompletedBookingsCount())
                .skills(user.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                .build();
    }
}
