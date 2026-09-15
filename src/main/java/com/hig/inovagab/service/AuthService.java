package com.hig.inovagab.service;

import com.hig.inovagab.dto.DtoAuthenticationRequest;
import com.hig.inovagab.dto.DtoAuthenticationResponse;
import com.hig.inovagab.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

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
}
