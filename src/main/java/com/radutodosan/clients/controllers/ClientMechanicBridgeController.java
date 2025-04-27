package com.radutodosan.clients.controllers;

import com.radutodosan.clients.dtos.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/client/mechanics")
@RequiredArgsConstructor
public class ClientMechanicBridgeController {

    private final RestTemplate restTemplate;

    @Value("${mechanics.service.url}")
    private String mechanicsServiceUrl;

    @GetMapping
    public ResponseEntity<?> getAvailableMechanics(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "User not authenticated", null));
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    mechanicsServiceUrl + "/mechanics/all",
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>(false, "Failed to retrieve mechanics", null));
        }
    }

}
