package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.ninja.*;
import com.konoha.NinjaMissionManager.security.NinjaUserDetail;
import com.konoha.NinjaMissionManager.security.jwt.JwtService;
import com.konoha.NinjaMissionManager.services.NinjaService;
import com.konoha.NinjaMissionManager.services.NinjaVillageCoordinatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticación y Registro", description = "Endpoints para el registro de nuevos ninjas y la autenticación de usuarios.")
public class AuthController {
    private final NinjaService ninjaService;
    private final AuthenticationManager authenticationManager;
    private final NinjaVillageCoordinatorService ninjaVillageCoordinatorService;
    private final JwtService jwtService;

    @Operation(summary = "Registrar un nuevo ninja")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ninja registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "409", description = "Email o nombre de ninja ya en uso")
    })
    @PostMapping("/register")
    public ResponseEntity<NinjaResponse> register(@RequestBody @Valid NinjaRegisterRequest ninjaRequest){
        NinjaResponse response = ninjaVillageCoordinatorService.registerNewNinja(ninjaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Registrar un nuevo ninja por un Kage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ninja creado exitosamente por un Kage"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (requiere rol KAGE)")
    })
    @PreAuthorize("hasRole('KAGE')")
    @PostMapping("/register/kage")
    public ResponseEntity<NinjaResponse> kageRegister(@RequestBody @Valid KageCreateNinjaRequest ninjaRequest){
        NinjaResponse response = ninjaVillageCoordinatorService.createNinja(ninjaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Autenticar un ninja",
            description = "Permite a un ninja iniciar sesión para obtener un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
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