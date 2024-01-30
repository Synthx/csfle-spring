package com.example.pocjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContextService {
    public String getCurrentCustomerNumber() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}
