package com.easylabour.userservice.config;

import com.easylabour.userservice.domain.model.Skill;
import com.easylabour.userservice.domain.model.User;
import com.easylabour.userservice.domain.model.UserType;
import com.easylabour.userservice.infrastructure.persistence.JpaSkillRepository;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserDataInitializer implements CommandLineRunner {

    private final JpaUserRepository userRepository;
    private final JpaSkillRepository skillRepository;

    @Override
    public void run(String... args) throws Exception {
        // Skills are managed in user-service, while users are created in auth-service
        // but they share the same table 'users'.
        // This initializer ensures skills are assigned to the seeded labourers.
        
        userRepository.findByEmail("labour1@example.com").ifPresent(user -> {
            if (user.getSkills() == null || user.getSkills().isEmpty()) {
                Skill plumbing = skillRepository.findByName("Plumbing")
                        .orElseGet(() -> skillRepository.save(Skill.builder().name("Plumbing").build()));
                user.setSkills(Set.of(plumbing));
                userRepository.save(user);
                System.out.println("Assigned Plumbing skill to labour1");
            }
        });

        userRepository.findByEmail("labour2@example.com").ifPresent(user -> {
            if (user.getSkills() == null || user.getSkills().isEmpty()) {
                Skill electric = skillRepository.findByName("Electric")
                        .orElseGet(() -> skillRepository.save(Skill.builder().name("Electric").build()));
                user.setSkills(Set.of(electric));
                userRepository.save(user);
                System.out.println("Assigned Electric skill to labour2");
            }
        });
    }
}
