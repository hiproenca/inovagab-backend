package com.hig.inovagab.controller;

import com.hig.inovagab.dto.DtoAuthenticationRequest;
import com.hig.inovagab.dto.DtoAuthenticationResponse;
import com.hig.inovagab.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<DtoAuthenticationResponse> authenticate(@RequestBody DtoAuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
