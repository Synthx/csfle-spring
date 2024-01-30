package com.example.pocjwt.controller;

import com.example.pocjwt.dao.entity.UserEntity;
import com.example.pocjwt.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {
    private final UserRepository userRepository;

    @GetMapping
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @PostMapping
    public UserEntity create(@RequestBody UserEntity entity) {
        return userRepository.save(entity);
    }
}
