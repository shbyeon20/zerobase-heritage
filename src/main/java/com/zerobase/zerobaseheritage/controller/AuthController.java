package com.zerobase.zerobaseheritage.controller;

import com.zerobase.zerobaseheritage.model.dto.SignInRequest;
import com.zerobase.zerobaseheritage.model.dto.SignUpRequest;
import com.zerobase.zerobaseheritage.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest request) {
        String token = authService.signIn(request);
        return ResponseEntity.ok(token);
    }
}
