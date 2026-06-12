package com.medibook.controller;

import com.medibook.dto.LoginRequest;
import com.medibook.dto.LoginResponse;
import com.medibook.dto.RegisterRequest;
import com.medibook.service.AuditoriaService;
import com.medibook.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final AuditoriaService auditoriaService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request);
        auditoriaService.registrar("LOGIN", "USUARIO", null,
                "Login exitoso: " + request.getEmail(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request,
                                           HttpServletRequest httpRequest) {
        String response = authService.register(request);
        auditoriaService.registrar("REGISTRO", "USUARIO", null,
                "Nuevo usuario registrado: " + request.getEmail(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(response);
    }
}