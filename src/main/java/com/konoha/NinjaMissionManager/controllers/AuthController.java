package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.ninja.*;
import com.konoha.NinjaMissionManager.security.NinjaUserDetail;
import com.konoha.NinjaMissionManager.security.jwt.JwtService;
import com.konoha.NinjaMissionManager.services.NinjaService;
import com.konoha.NinjaMissionManager.services.NinjaVillageCoordinatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final NinjaService ninjaService;
    private final AuthenticationManager authenticationManager;
    private final NinjaVillageCoordinatorService ninjaVillageCoordinatorService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<NinjaResponse> register(@RequestBody @Valid NinjaRegisterRequest ninjaRequest){
        NinjaResponse response = ninjaVillageCoordinatorService.registerNewNinja(ninjaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('KAGE')")
    @PostMapping("/register/kage")
    public ResponseEntity<NinjaResponse> kageRegister(@RequestBody @Valid KageCreateNinjaRequest ninjaRequest){
        NinjaResponse response = ninjaVillageCoordinatorService.createNinja(ninjaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid NinjaLoginRequest ninjaRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(ninjaRequest.email(), ninjaRequest.password())
        );

        NinjaUserDetail userDetail = (NinjaUserDetail) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetail);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
