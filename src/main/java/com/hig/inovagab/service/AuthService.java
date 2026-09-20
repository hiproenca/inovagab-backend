package com.hig.inovagab.service;

import com.hig.inovagab.dto.DtoAuthenticationRequest;
import com.hig.inovagab.dto.DtoAuthenticationResponse;
import com.hig.inovagab.dto.DtoRegisterRequest;
import com.hig.inovagab.dto.DtoUserResponse;
import com.hig.inovagab.model.User;
import com.hig.inovagab.model.UserRole;
import com.hig.inovagab.repository.UserRepository;
import com.hig.inovagab.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DtoAuthenticationResponse authenticate(DtoAuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String jwtToken = jwtService.generateToken(user);

        return buildResponse(jwtToken, user);
    }

    public DtoAuthenticationResponse register(DtoRegisterRequest request) {

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.OPERATOR)
                .build();

        User saved = repository.save(user);

        String jwtToken = jwtService.generateToken(saved);

        return buildResponse(jwtToken, saved);
    }

    private DtoAuthenticationResponse buildResponse(String token, User user) {
        DtoUserResponse userResponse = DtoUserResponse.builder()
                .id(user.getId())
                .name(user.getName() != null ? user.getName() : user.getEmail())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return DtoAuthenticationResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }
}