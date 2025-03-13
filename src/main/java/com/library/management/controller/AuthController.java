package com.library.management.controller;

import com.library.management.dto.AuthRequest;
import com.library.management.dto.AuthResponse;
import com.library.management.dto.UserDto;
import com.library.management.dto.RefreshTokenRequest;
import com.library.management.service.AuthService;
import com.library.management.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth-API", description = "Authentication operations")
@SecurityRequirements // No security requirements for auth endpoints for swagger
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(authService.register(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT is stateless, so we don't need to do anything on the server
        // The client should remove the token from storage
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto userDto = userService.getUserByUsername(authService.getCurrentUser().getUsername());
        return ResponseEntity.ok(userDto);
    }
}
