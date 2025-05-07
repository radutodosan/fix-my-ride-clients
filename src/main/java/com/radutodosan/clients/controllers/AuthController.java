package com.radutodosan.clients.controllers;

import com.radutodosan.clients.dtos.*;
import com.radutodosan.clients.entities.Client;
import com.radutodosan.clients.services.ClientService;
import com.radutodosan.clients.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/clients")
@RequiredArgsConstructor
public class AuthController {

    private final ClientService clientService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@RequestBody SignupRequestDTO request) {
        try {
            clientService.signup(request);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Client registered successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponseDTO<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<JwtResponse>> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            Client client = clientService.authenticate(loginRequest);

            String accessToken = jwtUtil.generateAccessToken(client.getUsername());
            ResponseCookie responseCookie = jwtUtil.generateResponseCookie(client.getUsername());

            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

            JwtResponse jwtResponse = new JwtResponse(accessToken, client);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful", jwtResponse));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(new ApiResponseDTO<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentClient(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            ApiResponseDTO<?> errorResponse = new ApiResponseDTO<>(
                    false,
                    "Client is not authenticated",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            String username = userDetails.getUsername();
            Client client = clientService.getByUsername(username);
            ClientDetailsDTO clientDetails = ClientDetailsDTO.builder()
                    .username(client.getUsername())
                    .email(client.getEmail())
                    .pictureUrl(client.getPictureUrl())
                    .build();
            ApiResponseDTO<?> response = new ApiResponseDTO<>(
                    true,
                    "Client retrieved successfully",
                    clientDetails
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDTO<?> errorResponse = new ApiResponseDTO<>(
                    false,
                    "Client not found: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Refresh token missing", null));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Invalid or expired refresh token", null));
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);

        Map<String, String> tokenData = Map.of(
                "accessToken", newAccessToken
        );

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Access token refreshed successfully", tokenData));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<?>> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = jwtUtil.deleteResponseCookie();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Logged out successfully", null));
    }



}
