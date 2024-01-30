package com.example.pocjwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.pocjwt.service.ContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final ContextService contextService;

    @PostMapping("login")
    public String login(String customerNumber) {
        final var algorithm = Algorithm.HMAC512("ceci-est-un-secret");

        return JWT.create()
                .withIssuer("auth0")
                .withSubject(customerNumber)
                .sign(algorithm);
    }

    @GetMapping("current")
    public String getCurrent() {
        return contextService.getCurrentCustomerNumber();
    }
}
