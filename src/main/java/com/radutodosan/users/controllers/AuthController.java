package com.radutodosan.users.controllers;

import com.radutodosan.users.dtos.*;
import com.radutodosan.users.entities.AppUser;
import com.radutodosan.users.services.AppUserService;
import com.radutodosan.users.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@RequestBody SignupRequestDTO request) {
        try {
            appUserService.signup(request);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "User registered successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponseDTO<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<JwtResponse>> login(@RequestBody LoginRequestDTO request) {
        try {
            AppUser user = appUserService.authenticate(request);
            String token = jwtUtil.generateToken(user.getUsername());
            JwtResponse jwtResponse = new JwtResponse(token, user);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful", jwtResponse));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(new ApiResponseDTO<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            ApiResponseDTO<?> errorResponse = new ApiResponseDTO<>(
                    false,
                    "User is not authenticated",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            String username = userDetails.getUsername();
            AppUser mechanic = appUserService.getByUsername(username);
            AppUserDetailsDTO mechanicDetails = AppUserDetailsDTO.builder()
                    .username(mechanic.getUsername())
                    .email(mechanic.getEmail())
                    .build();
            ApiResponseDTO<?> response = new ApiResponseDTO<>(
                    true,
                    "User retrieved successfully",
                    mechanicDetails
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDTO<?> errorResponse = new ApiResponseDTO<>(
                    false,
                    "User not found: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }




}
