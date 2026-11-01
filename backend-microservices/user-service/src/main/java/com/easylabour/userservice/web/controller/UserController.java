package com.easylabour.userservice.web.controller;

import com.easylabour.common.dto.UserDto;
import com.easylabour.userservice.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.createUser(userDto));
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchBySkill(@RequestParam String skill) {
        return ResponseEntity.ok(userService.searchBySkill(skill));
    }

    @PutMapping("/{id}/skills")
    public ResponseEntity<UserDto> updateSkills(@PathVariable String id, @RequestBody java.util.Set<String> skills) {
        return ResponseEntity.ok(userService.updateSkills(id, skills));
    }
}
