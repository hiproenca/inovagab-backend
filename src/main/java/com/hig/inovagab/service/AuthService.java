package com.hig.inovagab.service;

import com.hig.inovagab.dto.DtoAuthenticationRequest;
import com.hig.inovagab.dto.DtoAuthenticationResponse;
import com.hig.inovagab.dto.DtoRegisterRequest;
import com.hig.inovagab.model.User;
import com.hig.inovagab.model.UserRole;
import com.hig.inovagab.repository.UserRepository;
import com.hig.inovagab.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    //authenticate
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;


    //register
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DtoAuthenticationResponse authenticate(DtoAuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());

        String jwtToken = jwtService.generateToken(user);


        return DtoAuthenticationResponse.builder().token(jwtToken).build();
    }

    public DtoAuthenticationResponse register(DtoRegisterRequest request) {

        var user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.OPERATOR).build();

        repository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return DtoAuthenticationResponse.builder().token(jwtToken).build();
    }
}
